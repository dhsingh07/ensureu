// MongoDB script to convert co and so fields from integers to strings
// Run this in MongoDB shell with authentication:
// mongosh "mongodb://username:password@localhost:27017/assessu" < migrate_co_so_to_string.js
// Or without auth (if MongoDB is configured without authentication):
// mongosh assessu < migrate_co_so_to_string.js

// Function to convert array values to strings
function convertToStringArray(arr) {
    if (!arr) return [];
    if (!Array.isArray(arr)) return [String(arr)];
    return arr.map(v => String(v));
}

// Update freePaperCollection
print("Updating freePaperCollection...");
db.freePaperCollection.find({}).forEach(function(doc) {
    let updated = false;

    if (doc.pattern && doc.pattern.sections) {
        doc.pattern.sections.forEach(function(section) {
            // Update questions in section.questionData
            if (section.questionData && section.questionData.questions) {
                section.questionData.questions.forEach(function(q) {
                    if (q.problem) {
                        if (q.problem.co !== undefined) {
                            q.problem.co = convertToStringArray(q.problem.co);
                            updated = true;
                        }
                        if (q.problem.so !== undefined) {
                            q.problem.so = convertToStringArray(q.problem.so);
                            updated = true;
                        }
                    }
                    if (q.problemHindi && q.problemHindi.co !== undefined) {
                        q.problemHindi.co = convertToStringArray(q.problemHindi.co);
                        q.problemHindi.so = convertToStringArray(q.problemHindi.so);
                        updated = true;
                    }
                });
            }

            // Update questions in subSections
            if (section.subSections) {
                section.subSections.forEach(function(sub) {
                    if (sub.questionData && sub.questionData.questions) {
                        sub.questionData.questions.forEach(function(q) {
                            if (q.problem) {
                                if (q.problem.co !== undefined) {
                                    q.problem.co = convertToStringArray(q.problem.co);
                                    updated = true;
                                }
                                if (q.problem.so !== undefined) {
                                    q.problem.so = convertToStringArray(q.problem.so);
                                    updated = true;
                                }
                            }
                            if (q.problemHindi && q.problemHindi.co !== undefined) {
                                q.problemHindi.co = convertToStringArray(q.problemHindi.co);
                                q.problemHindi.so = convertToStringArray(q.problemHindi.so);
                                updated = true;
                            }
                        });
                    }
                });
            }
        });
    }

    if (updated) {
        db.freePaperCollection.updateOne({_id: doc._id}, {$set: {pattern: doc.pattern}});
        print("Updated free paper: " + doc._id);
    }
});

// Update paidPaperCollection
print("Updating paidPaperCollection...");
db.paidPaperCollection.find({}).forEach(function(doc) {
    let updated = false;

    if (doc.pattern && doc.pattern.sections) {
        doc.pattern.sections.forEach(function(section) {
            // Update questions in section.questionData
            if (section.questionData && section.questionData.questions) {
                section.questionData.questions.forEach(function(q) {
                    if (q.problem) {
                        if (q.problem.co !== undefined) {
                            q.problem.co = convertToStringArray(q.problem.co);
                            updated = true;
                        }
                        if (q.problem.so !== undefined) {
                            q.problem.so = convertToStringArray(q.problem.so);
                            updated = true;
                        }
                    }
                    if (q.problemHindi && q.problemHindi.co !== undefined) {
                        q.problemHindi.co = convertToStringArray(q.problemHindi.co);
                        q.problemHindi.so = convertToStringArray(q.problemHindi.so);
                        updated = true;
                    }
                });
            }

            // Update questions in subSections
            if (section.subSections) {
                section.subSections.forEach(function(sub) {
                    if (sub.questionData && sub.questionData.questions) {
                        sub.questionData.questions.forEach(function(q) {
                            if (q.problem) {
                                if (q.problem.co !== undefined) {
                                    q.problem.co = convertToStringArray(q.problem.co);
                                    updated = true;
                                }
                                if (q.problem.so !== undefined) {
                                    q.problem.so = convertToStringArray(q.problem.so);
                                    updated = true;
                                }
                            }
                            if (q.problemHindi && q.problemHindi.co !== undefined) {
                                q.problemHindi.co = convertToStringArray(q.problemHindi.co);
                                q.problemHindi.so = convertToStringArray(q.problemHindi.so);
                                updated = true;
                            }
                        });
                    }
                });
            }
        });
    }

    if (updated) {
        db.paidPaperCollection.updateOne({_id: doc._id}, {$set: {pattern: doc.pattern}});
        print("Updated paid paper: " + doc._id);
    }
});

print("Migration complete!");
