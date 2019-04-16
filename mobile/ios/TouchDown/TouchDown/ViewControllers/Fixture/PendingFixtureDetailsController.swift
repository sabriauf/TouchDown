//
//  PendingFixtureDetailsController.swift
//  Touchdown
//
//  Created by Thisura on 3/7/19.
//

import Foundation
import UIKit
import MapKit

class PendingFixtureDetailsController: UIViewController{
    
    static let identifier = "PendingFixtureDetailsController"
    static func present(_ controller: UIViewController, _ fixture: FixtureViewModel.FixtureItemDetails){
        let sb = UIStoryboard(name: "Main", bundle: nil)
        let vc = sb.instantiateViewController(withIdentifier: identifier) as! PendingFixtureDetailsController
        vc.modalPresentationStyle = .overCurrentContext
        vc.modalTransitionStyle = .crossDissolve
        vc.fixtureItem = fixture
        controller.present(vc, animated: true, completion: nil)
    }
    
    @IBOutlet weak var mapView: MKMapView!
    @IBOutlet weak var matchLabel: UILabel!
    @IBOutlet weak var dateTimeLabel: UILabel!
    @IBOutlet weak var showInMap: UIButton!
    @IBOutlet weak var showInMapShadow: UIView!
    @IBOutlet weak var addReminder: UIButton!
    @IBOutlet weak var addReminderShadow: UIView!
    @IBOutlet weak var container: UIView!
    
    var fixtureItem: FixtureViewModel.FixtureItemDetails!
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        setupLook()
        setupText()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        setupMap()
    }
    
    override func touchesEnded(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesEnded(touches, with: event)
        if let touch = touches.first{
            let pot = touch.location(in: view)
            if !container.frame.contains(pot){
                dismiss(animated: true, completion: nil)
            }
        }
    }
    
    func setupLook(){
        container.layer.cornerRadius = 8.0
        container.clipsToBounds = true
        
        showInMap.layer.cornerRadius = 4.0
        addReminder.layer.cornerRadius = 4.0
        setShadowOnButton(showInMapShadow, refV: showInMap)
        setShadowOnButton(addReminderShadow, refV: addReminder)
    }
    
    func setupText(){
        let f = DateFormatter()
        f.dateFormat = "dd MMM yyyy HH:mm"
        dateTimeLabel.text = f.string(from: fixtureItem!.match!.date)
        
        DispatchQueue.global().async { [weak self] in
            if let s = self{
                let match = s.fixtureItem!.match!
                let team1 = TeamDAO.getTeam(withId: match.teamOne)
                let team2 = TeamDAO.getTeam(withId: match.teamTwo)
                
                DispatchQueue.main.async {
                    s.matchLabel.text = String(
                        format: "%@ vs. %@",
                        team1!.name,
                        team2!.name
                    )
                }
            }
        }
    }
    
    func setupMap(){
        let locCoord = fixtureItem.pendingData!.matchLocation!
        let loc = CLLocation(latitude: locCoord.latitude, longitude: locCoord.longitude)
        let regionRadius: CLLocationDistance = 800
        let region = MKCoordinateRegionMakeWithDistance(loc.coordinate, regionRadius, regionRadius)
        let annotation = MKPointAnnotation()
        annotation.title = "Match Location"
        annotation.coordinate = loc.coordinate
        
        mapView.setRegion(region, animated: true)
        mapView.addAnnotation(annotation)
    }
    
    private func setShadowOnButton(_ shadowV: UIView, refV: UIView){
        //let r = refV.layer.cornerRadius
        //shadowV.layer.shadowPath = UIBezierPath(roundedRect: refV.bounds, cornerRadius: r).cgPath
        shadowV.layer.shadowOpacity = 0.1
        shadowV.layer.shadowOffset = CGSize(width: 0.0, height: 2.0)
        shadowV.layer.shadowRadius = 2.0
    }
    
    @IBAction func showInMapPressed(){
        let loc = fixtureItem!.pendingData!.matchLocation
        let latitude:CLLocationDegrees =  loc!.latitude
        let longitude:CLLocationDegrees =  loc!.longitude
        
        let regionDistance:CLLocationDistance = 1000
        let coordinates = CLLocationCoordinate2DMake(latitude, longitude)
        let regionSpan = MKCoordinateRegionMakeWithDistance(coordinates, regionDistance, regionDistance)
        let options = [
            MKLaunchOptionsMapCenterKey: NSValue(mkCoordinate: regionSpan.center),
            MKLaunchOptionsMapSpanKey: NSValue(mkCoordinateSpan: regionSpan.span)
        ]
        let placemark = MKPlacemark(coordinate: coordinates, addressDictionary: nil)
        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = matchLabel.text! + " Match Location"
        mapItem.openInMaps(launchOptions: options)
    }
    
    @IBAction func addReminderTapped(){
        ReminderHelper.showCalendar(fixtureItem!.match!.date)
    }
    
}

extension PendingFixtureDetailsController: MKMapViewDelegate{
    
    func mapView(_ mapView: MKMapView, viewFor annotation: MKAnnotation) -> MKAnnotationView? {
        guard annotation is MKPointAnnotation else { return nil }
        
        let identifier = "Annotation"
        var annotationView = mapView.dequeueReusableAnnotationView(withIdentifier: identifier)
        
        if annotationView == nil {
            annotationView = MKPinAnnotationView(annotation: annotation, reuseIdentifier: identifier)
            annotationView!.canShowCallout = true
        } else {
            annotationView!.annotation = annotation
        }
        
        return annotationView
    }
    
}
