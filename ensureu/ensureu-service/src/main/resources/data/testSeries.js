id = Math.random().toString(36).replace(/[0-0]+[.]/g, '').substr(0, 11)
db.testSeries.insert([{
    "_id": id,
    "testSeriesId": id,
    "description": "CGL Test Series",
    "price": 500,
    "discountedPrice": 350,
    "discountedPercentage": 30,
    "active": true,
    "subscriptionInfoList": [{
        "subscriptionType": "QUATERLY",
        "paperHierarchyId": "",
        "paperCount": 10,
    }, {
        "subscriptionType": "QUATERLY",
        "paperHierarchyId": "",
        "paperCount": 10,
    }],
    "createdDate": 0,
    "validity": 0,
    "amendmentNo": 0,
    "createdBy": "Manish",
    "modifiedBy": "Manish"
}])
