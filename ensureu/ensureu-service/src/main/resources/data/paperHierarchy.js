var paperType = ["BANK", "SSC"];
var testType = ["FREE", "PAID", "PRACTICE", "QUIZ", "PASTPAPER", "RECOMMENDED", "ALL"];
var papercategory = ["SSC_CGL", "SSC_CPO", "SSC_CHSL", "BANK_PO"];
var papersubcategory = ["SSC_CGL_TIER1", "SSC_CGL_TIER2", "SSC_CHSL_TIER2", "SSC_CHSL_TIER1", "SSC_CPO_TIER1",
    "SSC_CPO_TIER2", "BANK_PO_PRE", "BANK_PO_MAIN"];
var mapping = [
    {"BANK": ["BANK_PO"]},
    {"SSC": ["SSC_CGL", "SSC_CPO", "SSC_CHSL"]},
    {"SSC_CGL": ["SSC_CGL_TIER1", "SSC_CGL_TIER2"]},
    {"SSC_CHSL": ["SSC_CHSL_TIER2", "SSC_CHSL_TIER1"]},
    {"SSC_CPO": ["SSC_CPO_TIER1", "SSC_CPO_TIER2"]},
    {"BANK_PO":["BANK_PO_PRE", "BANK_PO_MAIN"]}
]

var map =  new Object();

map["BANK"] = ["BANK_PO"]
map["BANK_PO"]= ["BANK_PO_PRE", "BANK_PO_MAIN"]
map["SSC"] = ["SSC_CGL", "SSC_CPO", "SSC_CHSL"]
map["SSC_CGL"]= ["SSC_CGL_TIER1", "SSC_CGL_TIER2"]
map["SSC_CHSL"]= ["SSC_CHSL_TIER2", "SSC_CHSL_TIER1"]
map["SSC_CPO"]= ["SSC_CPO_TIER1", "SSC_CPO_TIER2"]

// as of now making every combination

for (var i = 0; i < paperType.length; i++) {
    var paperT = paperType[i]
    var paperCatArr = map[paperT]
    for (var j = 0; j < testType.length; j++) {
      var testT = testType[j]
        for(var k =0;k < paperCatArr.length;k++){
            var paperC = paperCatArr[k]
            var paperSCArr = map[paperC]
            for(var l =0;l < paperSCArr.length;l++){

                var id = Math.random().toString(36).replace(/[0-0]+[.]/g, '').substr(0, 11)
                db.paperHierarchy.insert({
                    "_id": id,
                    "paperType": paperType[i],
                    "testType": testType[j],
                    "paperCategory": paperCatArr[k],
                    "paperSubCategory": paperSCArr[l]
                })
            }
        }
    }
}


