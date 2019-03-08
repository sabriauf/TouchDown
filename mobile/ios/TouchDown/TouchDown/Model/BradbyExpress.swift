import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class BradbyExpress: Mappable{
    
    var imageUrl: String = ""
    var redirectUrl: String = ""
    
    
    struct PropertyKey{
        static let imageUrl = "img_url"
        static let redirectUrl = "redirect_url"
    }
    
    init(){
    }
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        self.imageUrl <- map[PropertyKey.imageUrl]
        self.redirectUrl <- map[PropertyKey.redirectUrl]
    }
    
}
