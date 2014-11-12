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

    .controller('editorController', function ($scope, $modal, UserService, ConversationService) {
                    $scope.sendOnEnterEnabled = true;

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

                    $scope.openModal = function () {
                        var modalInstance = $modal.open(
                            {
                                templateUrl: 'editor/modals/convModal.html',
                                controller: 'ModalInstanceCtrl',
                                resolve: {}
                            });

                        modalInstance.result.then(function (obj) {
                            ConversationService.startConversation(obj.selected, obj.name);
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

                    $scope.init();
                })

    .controller('ModalInstanceCtrl', function ($scope, $modalInstance, $http, UserService) {
                    $scope.currentUser = UserService.currentUser();
                    $scope.checked = [];
                    $scope.name = null;

                    $http.get('http://54.77.232.158:9000/users/available').success(function (data) {
                        $scope.users = data.online;
                    });

                    $scope.start = function () {
                        var obj = {
                            selected: $scope.checked,
                            name: $scope.name != null? $scope.name : $scope.currentUser
                        };
                        $modalInstance.close(obj);
                    };

                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                });