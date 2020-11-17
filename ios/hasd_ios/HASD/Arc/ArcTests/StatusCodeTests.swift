//
// StatusCodeTests.swift
//



import XCTest
@testable import Arc

class StatusCodeTests: XCTestCase {
    let statusCodes = [-1, 50, 100, 200, 201, 202, 22, 300, 305, 401, 409, 404]

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }
    func testRequest() throws {
        let e = expectation(description: "api_request")
        var r = HMRequest<String>.performRequest(method: BackendRequestMethod.get, endPoint:"https://www.google.com/search?q=google") { (res, obj, err) in
            let code = StatusCode.with(response: res)
            print(code.code as Any, code.succeeded, code.failureMessage as Any)

            XCTAssertNotNil(code.code)
            XCTAssertTrue(code.succeeded)
            XCTAssertNil(code.failureMessage)
            dump(res)
            e.fulfill()
        }
        wait(for: [e], timeout: 10)
        print(r)
    }
    func testBadRequest() throws {
        let e = expectation(description: "api_request")
        HMRestAPI.shared.setBaseURL(url:"https://google.com")
        HMAPI.baseUrl = "https://google.com"
        var r = HMRequest<String>.performRequest(method: BackendRequestMethod.get, endPoint:"/nothing") { (res, obj, err) in
            let code = StatusCode.with(response: res)
            XCTAssertNotNil(code.code)
            XCTAssertFalse(code.succeeded)
            XCTAssertNotNil(code.failureMessage)
            print(code.code as Any, code.succeeded, code.failureMessage as Any)
            dump(res)
            e.fulfill()
        }
        wait(for: [e], timeout: 10)
        print(r)

    }
    func testUnknown() throws {
        let code = StatusCode.with(response: nil)
        XCTAssertNil(code.code)
        XCTAssertFalse(code.succeeded)
        XCTAssertNotNil(code.failureMessage)
    }
    func testSuccess() throws {
        //Given
        for statusCode in statusCodes {
            let code = StatusCode.code(statusCode)
            if (200...299).contains(statusCode) {
                XCTAssert(code.succeeded)
            } else {
                XCTAssert(code.succeeded == false)

            }
            print(code, code.succeeded)
        }
    }

    ///The server should only send 200-299 for valid success otherwise there is an issue.
    func testFailureMessage() throws {
        //Given
        for statusCode in statusCodes {
            let code = StatusCode.code(statusCode)
            print(code, code.failureMessage ?? "No Message")

            if (200...299).contains(statusCode) {
                XCTAssertNil(code.failureMessage)
            } else {
                XCTAssertTrue(code.failureMessage?.isEmpty == false)

            }
        }
    }
}
