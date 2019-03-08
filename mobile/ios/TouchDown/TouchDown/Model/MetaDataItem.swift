import Alamofire
import ObjectMapper
import AlamofireObjectMapper

class MetaDataItem: Mappable{
    
    var id: String = ""
    var metaKey: String = ""
    var metaValue: String = ""
    
    struct PropertyKey{
        static let id = "id"
        static let metaKey = "meta_key"
        static let metaValue = "meta_value"
    }
    
    init(){
    }
    
    required init?(map: Map) {
    }
    
    func mapping(map: Map) {
        self.id <- map[PropertyKey.id]
        self.metaKey <- map[PropertyKey.metaKey]
        self.metaValue <- map[PropertyKey.metaValue]
    }
    
}
