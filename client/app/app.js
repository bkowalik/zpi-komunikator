'use strict';

// Declare app level module which depends on views, and components
angular.module('developerCommunicator', [
    'ui.bootstrap',
    'ngRoute',
    'developerCommunicator.editor',
    'developerCommunicator.contacts',
    'developerCommunicator.settings',
    'developerCommunicator.history',
    'developerCommunicator.login'
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .otherwise({redirectTo: '/login'});
    }]).
    factory( 'UserService', function() {
      var currentUser = null;
      var ws = null;
      var ws_flags = [];
      var message_callbacks = [];

      return {
        login: function(nick){
            currentUser = nick;
            ws = new WebSocket('wss://54.77.232.158/client/'+nick);
            ws.onopen = function(){
                console.log("open");
              ws_flags['started']=true;
            };
            ws.onmessage = function(e){
              var server_message = e.data;
              console.log(server_message);
              (message_callbacks[server_message.from])(server_message);
            };
            ws.onerror = function (evt) {
              console.log("ERR: " + evt.data);
            };
          },
        sendMessage : function(message, to){
            if (ws.readyState === 1) {
              ws.send(JSON.stringify({
                      from: currentUser,
                      to: to,
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
        addMessageListener: function(from, callback){
          message_callbacks[from] = callback;
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
        currentUser: function() { return currentUser; }
      };
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
          else {
              window.location.hash="/editor"
          }
        });
    });;
        
