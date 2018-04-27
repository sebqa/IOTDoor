const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref('/door/doorKnock').onUpdate(change => {
    
    
    const payload = {
            notification: {
                title: "Door Activity!",
                body: "Somebody knocked on your door!",
                icon: "default",
                sound:"default",
                vibrate:"true"
            }
        };
     //Create an options object that contains the time to live for the notification and the priority
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24
    };
    
    return admin.messaging().sendToTopic("pushNotifications", payload, options);
    
})