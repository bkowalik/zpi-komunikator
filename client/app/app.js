'use strict';

// Declare app level module which depends on views, and components
angular.module('developerCommunicator', [
    'ui.bootstrap',
    'ngRoute',
    'developerCommunicator.editor',
    'developerCommunicator.settings',
    'developerCommunicator.history',
    'developerCommunicator.login'
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .otherwise({redirectTo: '/login'});
    }]).
    factory( 'UserService', function($rootScope) {
      var currentUser = null;
      var ws = null;
      var ws_flags = [];
      var message_callbacks = [];
      var newConversationHandler;

      return {
        login: function(nick){
            currentUser = nick;
            ws = new WebSocket('wss://54.77.232.158/client/'+nick);
            ws.onopen = function(){
                console.log("open");
              ws_flags['started']=true;
            };
            ws.onmessage = function(e){
              var server_message = JSON.parse(e.data);
              console.log("Received message");
              console.log(server_message);
              console.log(server_message["kind"]);
              switch(server_message["kind"]){
                case "TextMessageType":
                  console.log("Message send back to ConvService");
                  if(!message_callbacks[server_message.id]){
                    newConversationHandler(server_message);
                  }else{
                    (message_callbacks[server_message.id])(server_message);
                  }
                break;
              }
            };
            ws.onerror = function (evt) {
              console.log("ERR: " + evt.data);
            };
            ws.onclose = function (evt) {
              console.log("close");
            };
          },
        sendMessage : function(message, to, id){
            if (ws.readyState === 1) {
              ws.send(JSON.stringify({
                      from: currentUser,
                      to: to,
                      id: id,
                      kind: 'TextMessageType',
                      payload: {
                        message : message
                      }
              }));
            }
            else{
                setTimeout(sendMessage(message), 200);
            }
          },
        logout: function() { 
            return currentUser==null;
         },
        addMessageListener: function(id, callback){
          message_callbacks[id] = callback;
        },
        isLoggedIn: function() {  
          return !!currentUser;
        },
        serverStarted: function(callback){
            var interval = setInterval(function () {
                if(ws_flags['started']){
                    clearInterval(interval);
                    callback();
                }
            }, 100);
        },
        currentUser: function() { return currentUser; },
        setNewConversationHandler: function(value){
          console.log("Registered handler");
          console.log(value);
          newConversationHandler = value;
        }
      };
    })
    .factory( 'ConversationService', function(UserService) {
      var conversations = [];
      var observerCallbacks = [];
      var receiveMessage = function(message){
            var conversation = _.find(conversations, function(conv){ return conv.id == message.id});
            conversation.chat.push({
                name : message.from,
                avatarInitials : "AM",
                avatarColor : "AAA",
                text : message.payload.message,
                time : message.date
            });

            notifyObservers();
        };
      var setConversation =  function(message){
          var users = message.to;
          users.push(UserService.currentUser());
          var conversation = {
              name: message.payload.message,
              id : message.id,
              code :  'public class Hello222{\n' +
              '\tpublic static void main(String[] args) {\n' +
              '\t\tSystem.out.print("Hello World");\n' +
              '\t}\n' +
              '}\n',
              language : 'java',
              contributors : users,
              chat: []
          };
          conversations.push(conversation);

          notifyObservers();

          angular.element(document).ready(function(){
              $('pre code').each(function(i, block) {
                  hljs.highlightBlock(block);
              });
          });
          UserService.addMessageListener(conversation.id, receiveMessage);
        };
      var notifyObservers = function(){
        angular.forEach(observerCallbacks, function(callback){
          callback(conversations);
        });
      };
      UserService.setNewConversationHandler(setConversation);
      return {
        registerObserverCallback : function(callback){
          observerCallbacks.push(callback);
        },
        startConversation : function(users, convName){
            users = _.keys(users);
            users.push(UserService.currentUser());
            var conversation = {
                name: convName,
                id : guid(),
                code :  'public class Hello222{\n' +
                '\tpublic static void main(String[] args) {\n' +
                '\t\tSystem.out.print("Hello World");\n' +
                '\t}\n' +
                '}\n',
                language : 'java',
                contributors : users,
                chat: []
            };
            conversations.push(conversation);

            angular.element(document).ready(function(){
                $('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
            });
            UserService.sendMessage(convName, conversation.contributors, conversation.id);
            UserService.addMessageListener(conversation.id, this.receiveMessage);
        },
        sendMessage : function(message, conversation){
            conversation.chat.push({
                name : UserService.currentUser(),
                avatarInitials : "AM",
                avatarColor : "AAA",
                text : message,
                time : (new Date()).toLocaleTimeString()
            });
            UserService.sendMessage(message, conversation.contributors, conversation.id);
        },
        receiveMessage : function(message){
            var conversation = _.find(conversations, function(conv){ return conv.id == message.id});
            conversation.chat.push({
                name : message.from,
                avatarInitials : "AM",
                avatarColor : "AAA",
                text : message.payload.message,
                time : message.date
            });
            notifyObservers();
        },
        notifyObservers : function(){
          angular.forEach(observerCallbacks, function(callback){
            callback(conversations);
          });
        },
        getConversations : function(){
          return conversations;
        }
      }

    })

    .controller('headerController', function ($scope, UserService) {
        $scope.user = UserService.currentUser();
        $scope.$watch(function () { return UserService.currentUser() }, function (newVal, oldVal) {
            if (typeof newVal !== 'undefined') {
                $scope.user = UserService.currentUser();
            }
        });
        $scope.$on('$routeChangeStart', function (event) {
          if (!UserService.isLoggedIn()) {
              window.location.hash="/login"
          }
        });
    });
        
var guid = (function() {
  function s4() {
    return Math.floor((1 + Math.random()) * 0x10000)
               .toString(16)
               .substring(1);
  }
  return function() {
    return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
           s4() + '-' + s4() + s4() + s4();
  };
})();