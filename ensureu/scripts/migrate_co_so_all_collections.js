// MongoDB script to convert co and so fields from integers to strings
// Run with: mongosh "mongodb://ensureu:Ensureu%40india123@localhost:27017/ensureu?authSource=ensureu" < migrate_co_so_all_collections.js

// Function to convert array values to strings
function convertToStringArray(arr) {
    if (!arr) return [];
    if (!Array.isArray(arr)) return [String(arr)];
    return arr.map(v => String(v));
}

// Function to process questions in a questionData object
function processQuestionData(questionData) {
    let updated = false;
    if (questionData && questionData.questions) {
        questionData.questions.forEach(function(q) {
            if (q.problem) {
                if (q.problem.co !== undefined && q.problem.co !== null) {
                    let oldCo = q.problem.co;
                    q.problem.co = convertToStringArray(q.problem.co);
                    if (JSON.stringify(oldCo) !== JSON.stringify(q.problem.co)) {
                        updated = true;
                    }
                }
                if (q.problem.so !== undefined && q.problem.so !== null) {
                    let oldSo = q.problem.so;
                    q.problem.so = convertToStringArray(q.problem.so);
                    if (JSON.stringify(oldSo) !== JSON.stringify(q.problem.so)) {
                        updated = true;
                    }
                }
            }
            if (q.problemHindi) {
                if (q.problemHindi.co !== undefined && q.problemHindi.co !== null) {
                    q.problemHindi.co = convertToStringArray(q.problemHindi.co);
                    updated = true;
                }
                if (q.problemHindi.so !== undefined && q.problemHindi.so !== null) {
                    q.problemHindi.so = convertToStringArray(q.problemHindi.so);
                    updated = true;
                }
            }
        });
    }
    return updated;
}

// Function to process sections (handles both section.questionData and subSections)
function processSections(sections) {
    let updated = false;
    if (sections) {
        sections.forEach(function(section) {
            // Process questions in section.questionData
            if (section.questionData) {
                if (processQuestionData(section.questionData)) {
                    updated = true;
                }
            }
            // Process questions in subSections
            if (section.subSections) {
                section.subSections.forEach(function(sub) {
                    if (sub.questionData) {
                        if (processQuestionData(sub.questionData)) {
                            updated = true;
                        }
                    }
                });
            }
        });
    }
    return updated;
}

// Function to process a paper document (handles pattern.sections)
function processPaperDoc(doc) {
    let updated = false;
    if (doc.pattern && doc.pattern.sections) {
        if (processSections(doc.pattern.sections)) {
            updated = true;
        }
    }
    // Some collections might have paper embedded
    if (doc.paper && doc.paper.pattern && doc.paper.pattern.sections) {
        if (processSections(doc.paper.pattern.sections)) {
            updated = true;
        }
    }
    return updated;
}

// Collections to process
var collections = [
    'freePaperCollection',
    'paidPaperCollection',
    'freePaper',
    'paidPaper',
    'quizPaper',
    'quizPaperCollection',
    'practicePaperCollection',
    'paperInfoDataModel',
    'paperMetaData'
];

// Process each collection
collections.forEach(function(collName) {
    print("\n=== Processing " + collName + " ===");
    var count = 0;

    try {
        db.getCollection(collName).find({}).forEach(function(doc) {
            if (processPaperDoc(doc)) {
                // Update both pattern and paper.pattern if they exist
                var updateObj = {};
                if (doc.pattern) {
                    updateObj.pattern = doc.pattern;
                }
                if (doc.paper && doc.paper.pattern) {
                    updateObj["paper.pattern"] = doc.paper.pattern;
                }

                if (Object.keys(updateObj).length > 0) {
                    db.getCollection(collName).updateOne(
                        {_id: doc._id},
                        {$set: updateObj}
                    );
                    count++;
                    print("Updated doc: " + doc._id);
                }
            }
        });
        print("Updated " + count + " documents in " + collName);
    } catch (e) {
        print("Error processing " + collName + ": " + e.message);
    }
});

// Also check for user paper mapping collections
print("\n=== Looking for user paper mapping collections ===");
db.getCollectionNames().filter(function(c) {
    return c.toLowerCase().includes('user') && c.toLowerCase().includes('paper');
}).forEach(function(collName) {
    print("\n=== Processing " + collName + " ===");
    var count = 0;

    try {
        db.getCollection(collName).find({}).forEach(function(doc) {
            if (processPaperDoc(doc)) {
                var updateObj = {};
                if (doc.pattern) {
                    updateObj.pattern = doc.pattern;
                }
                if (doc.paper && doc.paper.pattern) {
                    updateObj["paper.pattern"] = doc.paper.pattern;
                }

                if (Object.keys(updateObj).length > 0) {
                    db.getCollection(collName).updateOne(
                        {_id: doc._id},
                        {$set: updateObj}
                    );
                    count++;
                    print("Updated doc: " + doc._id);
                }
            }
        });
        print("Updated " + count + " documents in " + collName);
    } catch (e) {
        print("Error processing " + collName + ": " + e.message);
    }
});

print("\n=== Migration complete! ===");
