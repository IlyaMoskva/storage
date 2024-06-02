use storage

db.createCollection("files")

db.files.createIndex({ "filename": 1 }, { unique: true })
db.files.createIndex({ "visibility": 1 })
db.files.createIndex({ "tags": 1 })
db.files.createIndex({ "uploadDate": -1 })

db.createCollection("tags")

db.tags.createIndex({ "name": 1 }, { unique: true })