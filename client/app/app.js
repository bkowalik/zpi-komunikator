'use strict';

// Declare app level module which depends on views, and components
angular.module('developerCommunicator', [
    'ui.bootstrap',
    'ngRoute',
    'angular-md5',
    'developerCommunicator.editor',
    'developerCommunicator.settings',
    'developerCommunicator.history',
    'developerCommunicator.login'
])
    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider
                    .otherwise({redirectTo: '/login'});
            }])

    .factory('UserGraphic', function () {

                 return {
                     color: function (text) {
                         // str to hash
                         for (var i = 0, hash = 0; i<text.length; hash = text.charCodeAt(i++) + ((hash << 5) - hash));
                         // int/hash to hex
                         for (var i = 0, color = ""; i<3; color += ("00" + ((hash >> i++*8) & 0xFF).toString(16)).slice(-2));

                         return color;
                     },

                     initialsByLogin: function (login) {
                         return (login.charAt(0) + login.charAt(1)).toUpperCase()
                     },

                     initialsByFirstLastNames: function (firstName, lastName) {
                         return (firstName.charAt(0) + lastName.charAt(1)).toUpperCase()
                     }
                 }
             })

    .factory('UserService', function () {
                 var currentUser = null;
                 var ws = null;
                 var ws_flags = [];
                 var message_callbacks = [];
                 var code_callbacks = [];
                 var newConversationHandler;
                 var newCodeHandler;

                 return {
                     login: function (user) {
                         currentUser = user;
                         ws = new WebSocket('wss://54.77.232.158/client/' + user);

                         ws.onopen = function () {
                             console.log("open");
                             ws_flags['started'] = true;
                         };

                         ws.onmessage = function (event) {
                            var server_message = JSON.parse(event.data);

                            console.log("Received message: " + server_message.kind);

                            switch (server_message["kind"]) {
                                case "TextMessageType":
                                     console.log("Message send back to ConvService");
                                     if (!message_callbacks[server_message.id]) {
                                         newConversationHandler(server_message);
                                     } else {
                                         (message_callbacks[server_message.id])(server_message);
                                     }
                                     break;

                                case "DiffSyncType":
                                    switch (server_message["payload"]["kind"]){
                                        case "Diff":
                                            console.log("Receive diff");

                                            break;
                                        case "Text":
                                            console.log("Receive text");

                                            break;
                                        case "NewSession":
                                            console.log("Receive new session");
                                            //odpalenie funkcji ktor podpina nasluchiwanie na tym id i updatowanie kodu w tej rozmowie
                                            newCodeHandler(server_message);
                                            break;
                                    }
                                    break;
                             }
                         };

                         ws.onerror = function (event) {
                             console.log("ERR: " + event.data);
                         };

                         ws.onclose = function (event) {
                             console.log("close");
                         };
                     },

                     sendMessage: function (message, to, id) {
                         if (ws.readyState === 1) {
                             ws.send(JSON.stringify(
                                 {
                                     from: currentUser,
                                     to: to,
                                     id: id,
                                     kind: 'TextMessageType',
                                     payload: {
                                         message: message
                                     }
                                 })
                             );
                         }
                         else {
                             setTimeout(sendMessage(message), 200);
                         }
                     },

                     startNewDiffSession : function(conversation) {
                         if (ws.readyState === 1) {
                             ws.send(JSON.stringify(
                                {
                                    from: currentUser,
                                    to: conversation.contributors,
                                    id: conversation.id,
                                    kind: 'DiffSyncType',
                                    payload: {
                                        kind: 'NewSession',
                                        text: conversation.code
                                    }
                                })
                             );
                             console.log("New session started.")
                         } else {
                            console.log("WebSocket closed. Message was not sent.")
                         }
                     },

                     sendDiff: function(patch, hash, conversation){
                        console.log(patch);
                        if (ws.readyState === 1) {
                             ws.send(JSON.stringify(
                                {
                                    from: currentUser,
                                    to: conversation.contributors,
                                    id: conversation.id,
                                    kind: 'DiffSyncType',
                                    payload: {
                                        kind: 'Diff',
                                        diff: patch,
                                        md5: hash
                                    }
                                })
                             );
                             console.log("New session started.")
                         } else {
                            console.log("WebSocket closed. Message was not sent.")
                         }
                     },

                     logout: function () {
                         currentUser = null;
                         ws = null;
                         ws_flags = [];
                         message_callbacks = [];
                         code_callbacks = [];

                         return currentUser;
                     },

                     addMessageListener: function (id, callback) {
                         message_callbacks[id] = callback;
                     },

                     addCodeListener: function(id, callback){
                        code_callbacks[id] = callback;
                     },

                     isLoggedIn: function () {
                         return !!currentUser;
                     },

                     serverStarted: function (callback) {
                         var interval = setInterval(function () {
                             if (ws_flags['started']) {
                                 clearInterval(interval);
                                 callback();
                             }
                         }, 100);
                     },

                     currentUser: function () {
                         return currentUser;
                     },

                     setNewConversationHandler: function (value) {
                         console.log("Registered conv handler");

                         newConversationHandler = value;
                     },

                     setNewCodeHandler: function (value) {
                         console.log("Registered code handler");

                         newCodeHandler = value;
                     },

                     getWs: function() {
                         return ws;
                     }
                 };
             })

    .factory('ConversationService', function (UserService, UserGraphic, md5) {
                 var conversations = [];
                 var observerCallbacks = [];

                 var changeCount = [];
                 var sendTimeOut = [];

                 var receiveMessage = function (message) {
                     var conversation = _.find(conversations, function (conv) {
                         return conv.id == message.id
                     });
                     var userName = message.from;

                     conversation.new_message = true;

                     conversation.chat.push(
                         {
                             name: userName,
                             avatarInitials: UserGraphic.initialsByLogin(userName),
                             avatarColor: UserGraphic.color(userName),
                             text: message.payload.message,
                             time: message.date
                         });

                     notifyObservers();
                 };

                 var receiveCode = function (message) {
                     var conversation = _.find(conversations, function (conv) {
                         return conv.id == message.id
                     });
                     var userName = message.from;

                     conversation.new_message = true;

                     console.log(message);

                     notifyObservers();
                 };

                 var setCode = function(message) {
                    var conversation = _.find(conversations, function (conv) {
                         return conv.id == message.id
                     });
                    conversation.code = message.payload.text;
                    conversation.shadow_code = message.payload.text;

                    notifyObservers();
                 };

                 var setConversation = function (message) {
                     var users = message.to;
                     users.push(UserService.currentUser());

                     var conversation = {
                         name: message.payload.message,
                         id: message.id,
                         code: "",
                         shadow_code: "",
                         language: 'java',
                         contributors: users,
                         chat: []
                     };
                     conversations.push(conversation);

                     notifyObservers();

                     angular.element(document).ready(function () {
                         $('pre code').each(function (i, block) {
                             hljs.highlightBlock(block);
                         });
                     });

                     UserService.addMessageListener(conversation.id, receiveMessage);
                     UserService.addCodeListener(conversation.id, receiveCode);
                 };

                 var notifyObservers = function () {
                     angular.forEach(observerCallbacks, function (callback) {
                         callback(conversations);
                     });
                 };

                 var sendCodeToServer = function (conv) {
                     var diff_tool = new diff_match_patch();
                     var patchRaw = diff_tool.patch_make(conv.shadow_code, conv.code);
                     var patch = diff_tool.patch_toText(patchRaw);
                     //console.log(diff_tool.diff_prettyHtml(diff))
                     conv.shadow_code = conv.code;
                     var hash = md5.createHash(conv.code);

                     UserService.sendDiff(patch, hash, conv);
                 };

                 UserService.setNewConversationHandler(setConversation);
                 UserService.setNewCodeHandler(setCode);

                 // var startNewDiffSession = function(conversation) {
                 //     var ws = UserService.getWs();
                 //     if (ws.readyState === 1) {
                 //         ws.send(JSON.stringify(
                 //                     {
                 //                         from: UserService.currentUser(),
                 //                         to: conversation.contributors,
                 //                         id: conversation.id,
                 //                         kind: 'DiffSyncType',
                 //                         payload: {
                 //                             kind: 'NewSession',
                 //                             text: conversation.code
                 //                         }
                 //                     })
                 //         );
                 //         console.log("New session started.")
                 //     } else {
                 //         console.log("WebSocket closed. Message was not sent.")
                 //     }
                 // };

                 return {
                     registerObserverCallback: function (callback) {
                         observerCallbacks.push(callback);
                     },

                     startConversation: function (users, convName, code) {
                         users = _.keys(users);
                         users.push(UserService.currentUser());

                         var conversation = {
                             name: convName,
                             id: guid(),
                             code: code,
                             shadow_code: code,
                             contributors: users,
                             chat: []
                         };
                         conversations.push(conversation);

                         angular.element(document).ready(function () {
                             $('pre code').each(function (i, block) {
                                 hljs.highlightBlock(block);
                             });
                         });

                         UserService.sendMessage(convName, conversation.contributors, conversation.id);
                         UserService.addMessageListener(conversation.id, this.receiveMessage);
                         UserService.startNewDiffSession(conversation);
                     },

                     sendMessage: function (message, conversation) {
                         var currentUser = UserService.currentUser();

                         conversation.chat.push(
                             {
                                 name: currentUser,
                                 avatarInitials: UserGraphic.initialsByLogin(currentUser),
                                 avatarColor: UserGraphic.color(currentUser),
                                 text: message,
                                 time: (new Date()).toLocaleTimeString()
                             });
                         UserService.sendMessage(message, conversation.contributors, conversation.id);
                     },

                     receiveMessage: function (message) {
                         var conversation = _.find(conversations, function (conv) {
                             return conv.id == message.id
                         });
                         var userName = message.from;

                         conversation.new_message = true;

                         conversation.chat.push(
                             {
                                 name: userName,
                                 avatarInitials: UserGraphic.initialsByLogin(userName),
                                 avatarColor: UserGraphic.color(userName),
                                 text: message.payload.message,
                                 time: message.date
                             });
                         notifyObservers();
                     },

                     notifyObservers: function () {
                         angular.forEach(observerCallbacks, function (callback) {
                             callback(conversations);
                         });
                     },

                     getConversations: function () {
                         return conversations;
                     },

                     changeCode: function(code, conv) {
                        var conversation = _.find(conversations, function (conv_it) {
                            return conv_it.id == conv.id
                        });   
                        if(changeCount[conversation.id] === undefined) changeCount[conversation.id] = 0;

                        if(changeCount[conversation.id] == 10){
                            sendTimeOut[conversation.id] = null;
                            changeCount[conversation.id] = 0;
                            sendCodeToServer(conversation);
                        } 
                        else{
                            if(sendTimeOut[conversation.id] != undefined) clearTimeout(sendTimeOut[conversation.id]);
                            changeCount[conversation.id]++;
                            sendTimeOut[conversation.id] = setTimeout(function(){
                                sendCodeToServer(conversation);
                            },1000);
                        }
                        conversation.code = code;
                     }
                 }
             })

    .controller('headerController', function ($scope, UserService) {
                    $scope.user = UserService.currentUser();

                    $scope.$watch(function () {
                        return UserService.currentUser()
                    }, function (newVal, oldVal) {
                        if (typeof newVal !== 'undefined') {
                            $scope.user = UserService.currentUser();
                        }
                    });

                    $scope.$on('$routeChangeStart', function (event) {
                        if (!UserService.isLoggedIn()) {
                            window.location.hash = "/login"
                        }
                    });

                    $scope.userLogout = function () {
                        $scope.user = UserService.logout();
                    }
                });

var guid = (function () {
    function s4() {
        return Math.floor((1 + Math.random())*0x10000)
            .toString(16)
            .substring(1);
    }

    return function () {
        return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
            s4() + '-' + s4() + s4() + s4();
    };
})();
