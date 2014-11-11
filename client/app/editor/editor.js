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

    .controller('contactsController', function ($scope, $rootScope) {
                    $rootScope.contactsOnline = [
                        {
                            "name": {"title": "mr", "first": "nathaniel", "last": "baker"},
                            "location": "B45",
                            "phone": "(304)-407-6713",
                            "picture": {
                                "large": "http://api.randomuser.me/portraits/men/0.jpg"
                            }
                        },
                        {
                            "name": {"title": "mr", "first": "liam", "last": "robinson"},
                            "phone": "(803)-333-2499",
                            "picture": {
                                "large": "http://api.randomuser.me/portraits/men/33.jpg"
                            }
                        }];
                    $rootScope.contactsOffline = [
                        {
                            "location": "D12",
                            "login": "organicpanda958",
                            "phone": "(540)-841-4132",
                            "picture": {
                                "large": "http://api.randomuser.me/portraits/women/76.jpg"
                            }
                        },
                        {
                            "name": {"title": "mrs", "first": "harper"},
                            "location": "D34",
                            "email": "harper.medina98@example.com",
                            "login": "bigpanda573",
                            "picture": {
                                "large": "http://api.randomuser.me/portraits/women/30.jpg"
                            }
                        }];

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
                        console.log(ConversationService.getConversations());
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
                            name: $scope.name
                        };
                        $modalInstance.close(obj);
                    };

                    $scope.cancel = function () {
                        $modalInstance.dismiss('cancel');
                    };
                });