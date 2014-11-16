'use strict';

angular.module('developerCommunicator.editor', ['ngRoute', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider
                    .when('/editor',
                          {
                              templateUrl: 'editor/editor.html',
                              controller: 'editorController'

                          })
                    .when('/contacts',
                          {
                              templateUrl: 'contacts/contacts.html',
                              controller: 'contactsController'
                          }
                );
            }])

    .directive('ngEnter', function() {
                   return function(scope, element, attrs) {
                       element.bind("keydown keypress", function(event) {
                           if(event.which === 13) {
                               scope.$apply(function(){
                                   scope.$eval(attrs.ngEnter, {'event': event});
                               });

                               event.preventDefault();
                           }
                       });
                   };
               })

    .controller('contactsController', function ($scope, $rootScope, $http, UserGraphic, UserService) {
                    $scope.usersOnline = [];
                    $scope.usersOffline = [];
                    $scope.currentUser = UserService.currentUser();

                    $http.get('http://54.77.232.158:9000/users/available').success(function (data) {
                        var usersOnline = data.online;

                        resolveAvatarForEachUserAndPushToScope(data.online, $scope.usersOnline);

                        $http.get('http://54.77.232.158:9000/users/all').success(function (data) {
                            var usersOffline = $(data.users).not(usersOnline).get();

                            resolveAvatarForEachUserAndPushToScope(usersOffline, $scope.usersOffline)
                        });
                    });

                    var resolveAvatarForEachUserAndPushToScope = function(users, scope) {
                        users.forEach(function(user) {
                            scope.push(
                                {
                                    name: user,
                                    avatarInitials: UserGraphic.initialsByLogin(user),
                                    avatarColor: UserGraphic.color(user)
                                }
                            )
                        });
                    };

                    $scope.init = function () {
                        $scope.applyTooltip();
                    };

                    $scope.applyTooltip = function () {
                        angular.element(document).ready(function () {
                            $('[data-toggle="tooltip"]').tooltip();
                        });
                    };

                    $scope.init();
                })

    .controller('editorController', function ($scope, $modal, $http, UserService, ConversationService) {
                    $scope.sendOnEnterEnabled = true;
                    $scope.currentUser = UserService.currentUser();

                    $scope.$watch(function () {
                        return ConversationService.getConversations()
                    }, function (newVal, oldVal) {
                        if (typeof newVal !== 'undefined') {
                            $scope.conversations = ConversationService.getConversations();
                        }
                    });

                    $scope.updateConversation = function (convs) {
                        $scope.$apply(function () {
                            $scope.conversations = convs;
                        });
                    };

                    // $scope.$on('newConv', function(event,data) {
                    //     console.log(data);
                    //     $scope.conversations = data;
                    // });

                    $scope.clearNotification = function(conv){
                        conv.new_message = false;
                    }

                    ConversationService.registerObserverCallback($scope.updateConversation);

                    $scope.sendMessage = function (conversation) {
                        ConversationService.sendMessage($scope.message, conversation);
                        $scope.message = "";
                    };

                    $scope.sendMessageOnEnter = function (conversation) {
                        if($scope.sendOnEnterEnabled) {
                            ConversationService.sendMessage($scope.message, conversation);
                            $scope.message = "";
                        }
                    };

                    $scope.openNewConversationModal = function () {
                        var modalInstance = $modal.open(
                            {
                                templateUrl: 'editor/modals/convModal.html',
                                controller: 'ModalInstanceCtrl',
                                resolve: {}
                            });

                        modalInstance.result.then(function (obj) {
                            ConversationService.startConversation(obj.selected, obj.name, obj.code);
                        });
                    };

                    $scope.init = function () {
                        $scope.reloadHighlight();
                    };

                    $scope.reloadHighlight = function () {
                        angular.element(document).ready(function () {
                            $('pre code').each(function (i, block) {
                                hljs.highlightBlock(block);
                            });
                        });
                    };

                    $scope.codeChange = function(evt, conv) {
                        ConversationService.changeCode(evt.target.innerText, conv);
                        $scope.conversations = ConversationService.getConversations();
                    };

                    $scope.getMoreUsersOnline = function() {
                        $http.get('http://54.77.232.158:9000/users/available').success(function (data) {

                            $scope.moreUsersOnline =$(data.online).not($scope.conversations[$scope.actual_tab].contributors).get();
                        });
                    };

                    $scope.addUser = function(tab_nr, name) {
                        UserService.addUser($scope.conversations[tab_nr], name);

                        console.log("I'm going to add " + name);
                    };

                    $scope.removeUser = function(tab_nr, name) {
                        UserService.removeUser($scope.conversations[tab_nr], name);

                        console.log("I'm going to kick off " + name);
                    };

                    $scope.closeConversation = function(tab_nr, name) {
                        UserService.removeUser($scope.conversations[tab_nr], name);

                        console.log("I'm going to kick off " + name + ' from ' + tab_nr);
                    };

                    $scope.init();
                })

    .controller('ModalInstanceCtrl', function ($scope, $modalInstance, $http, UserService) {
                    $scope.currentUser = UserService.currentUser();
                    $scope.checked = [];
                    $scope.name = null;
                    $scope.code = "";

                    $http.get('http://54.77.232.158:9000/users/available').success(function (data) {
                        $scope.users = data.online;
                    });

                    $scope.start = function () {
                        var obj = {
                            selected: $scope.checked,
                            name: $scope.name != null? $scope.name : $scope.currentUser,
                            code: $scope.code
                        };
                        $modalInstance.close(obj);
                    };

                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };

        $scope.init = function () {
            $scope.isFileEnabled = checkFileAPI();
        };


        $scope.init();

        var reader;

        function checkFileAPI() {
            if (window.File && window.FileReader && window.FileList && window.Blob) {
                reader = new FileReader();
                console.log("FileReader API supported.")
                return true;
            } else {
                console.log("FileReader API not supported.")
                return false;
            }
        }

        /**
         * read text input
         */
        $scope.readText = function(filePath) {
            var output = "";
            if (filePath.files && filePath.files[0]) {
                reader.onload = function (e) {
                    output = e.target.result;

                    $scope.code = output;
                    $scope.name = filePath.files[0].name;
                    $scope.$apply();
                };
                reader.readAsText(filePath.files[0]);

            }
            else if (ActiveXObject && filePath) {
                try {
                    reader = new ActiveXObject("Scripting.FileSystemObject");
                    var file = reader.OpenTextFile(filePath, 1);
                    output = file.ReadAll();
                    file.Close();

                    $scope.code = output;
                    $scope.name = filePath.files[0].name;
                    $scope.$apply();
                } catch (e) {
                    if (e.number == -2146827859) {
                        alert('Unable to access local files due to browser security settings. ' +
                        'To overcome this, go to Tools->Internet Options->Security->Custom Level. ' +
                        'Find the setting for "Initialize and script ActiveX controls not marked as safe" and change it to "Enable" or "Prompt"');
                    }
                }
            }
            else {
                return false;
            }
            return true;
        }
    });
