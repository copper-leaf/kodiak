import UIKit

class SwiftClassWithSuppressedMembers {

    init() {

    }

    /**
    - suppress
    */
    convenience init(dontShowThisConstructor: Int) {
        self.init()
    }

    convenience init(showThisConstructor: String) {
        self.init()
    }

    /**
    - suppress
    */
    var dontShowThisProperty: Int? = 0
    var showThisProperty: String? = ""

    /**
    - suppress
    */
    func dontShowThisMethod() -> Int {
        return 0
    }
    func showThisMethod() -> String {
        return ""
    }
}
