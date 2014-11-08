'use strict';

// Declare app level module which depends on views, and components
angular.module('developerCommunicator', [
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

      return {
        login: function(nick){
            currentUser = nick;
            ws = new WebSocket('ws://localhost:9000/client/'+nick);
            ws.onopen = function(){
                console.log("open");
              ws_flags['started']=true;
            };
            ws.onmessage = function(e){
              var server_message = e.data;
              console.log(server_message);
              console.log("aaa");
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
            return currentUser!=null;
         },
        isLoggedIn: function() {  

        },
        serverStarted: function(callback){
            var interval = setInterval(function () {
                console.log("open222");
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
    });;
        
