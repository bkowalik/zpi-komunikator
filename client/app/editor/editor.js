'use strict';

angular.module('developerCommunicator.editor', ['ngRoute','ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/editor',
            {
            templateUrl: 'editor/editor.html',
            controller: 'editorController'
            }
        ).when('/contacts',
            {
            templateUrl: 'contacts/contacts.html',
            controller: 'contactsController'
            }
        );
    }])

    .controller('contactsController', function ($scope, $rootScope) {
        $rootScope.contactsOnline = [
            {
                "name": {"title": "mr", "first": "nathaniel", "last": "baker"},
                "location": "B45",
                "phone": "(304)-407-6713",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/men/0.jpg",
                }
            },
            {
                "name": {"title": "mr", "first": "liam", "last": "robinson"},
                "phone": "(803)-333-2499",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/men/33.jpg",
                }
            }];
        $rootScope.contactsOffline = [
            {
                "location": "D12",
                "login": "organicpanda958",
                "phone": "(540)-841-4132",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/women/76.jpg",
                }
            },
            {
                "name": {"title": "mrs", "first": "harper"},
                "location": "D34",
                "email": "harper.medina98@example.com",
                "login": "bigpanda573",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/women/30.jpg",
                }
            }];

        $scope.init = function () {
            $scope.applyTooltip();
        };

        $scope.applyTooltip = function(){
            angular.element(document).ready(function(){
                $('[data-toggle="tooltip"]').tooltip();
            });
        };

        $scope.init();
    })

    .controller('editorController', function ($scope, $modal, UserService, ConversationService) {
        //$rootScope.conversations = [
            // {
            //     name : 'Conversation 1',
            //     id : 'conv1',
            //     active : true,
            //     code :  'public class Hello111{\n' +
            //     '\tpublic static void main(String[] args) {\n' +
            //     '\t\tSystem.out.print("Hello World");\n' +
            //     '\t}\n' +
            //     '}\n',
            //     language : 'java',
            //     contributors : ['Piotr Brudny', 'Piotr Florczyk', 'Aleksander Maj'],
            //     chat : [
            //         {
            //             name : 'Piotr Brudny',
            //             avatarInitials : "PB",
            //             avatarColor : "ABC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:45'
            //         },
            //         {
            //             name : 'Piotr Florczyk',
            //             avatarInitials : "PF",
            //             avatarColor : "FBC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:46'
            //         },
            //         {
            //             name : 'Piotr Brudny',
            //             avatarInitials : "PB",
            //             avatarColor : "ABC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:45'
            //         },
            //         {
            //             name : 'Aleksander Maj',
            //             avatarInitials : "AM",
            //             avatarColor : "AAA",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:48'
            //         },
            //         {
            //             name : 'Piotr Florczyk',
            //             avatarInitials : "PF",
            //             avatarColor : "FBC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:52'
            //         },
            //         {
            //             name : 'Piotr Brudny',
            //             avatarInitials : "PB",
            //             avatarColor : "ABC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:55'
            //         },
            //         {
            //             name : 'Piotr Florczyk',
            //             avatarInitials : "PF",
            //             avatarColor : "FBC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:58'
            //         }
            //     ]
            // },
            // {
            //     name : 'Conversation 2',
            //     id : 'conv2',
            //     active : false,
            //     code :  'public class Hello222{\n' +
            //     '\tpublic static void main(String[] args) {\n' +
            //     '\t\tSystem.out.print("Hello World");\n' +
            //     '\t}\n' +
            //     '}\n',
            //     language : 'java',
            //     contributors : ['Piotr Florczyk', 'Aleksander Maj'],
            //     chat : [
            //         {
            //             name : 'Aleksander Maj',
            //             avatarInitials : "AM",
            //             avatarColor : "AAA",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:48'
            //         },
            //         {
            //             name : 'Piotr Florczyk',
            //             avatarInitials : "PF",
            //             avatarColor : "FBC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:52'
            //         },
            //         {
            //             name : 'Piotr Brudny',
            //             avatarInitials : "PB",
            //             avatarColor : "ABC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:55'
            //         },
            //         {
            //             name : 'Piotr Florczyk',
            //             avatarInitials : "PF",
            //             avatarColor : "FBC",
            //             text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
            //             time : '14:58'
            //         }
            //     ]
            // }
        //];

        $scope.$watch(function () { return ConversationService.conversations }, function (newVal, oldVal) {
            console.log(ConversationService.conversations);
            if (typeof newVal !== 'undefined') {
                $scope.conversations = ConversationService.conversations;
            }
        });

        $scope.init = function () {
            $scope.reloadHighlight();
        };

        $scope.reloadHighlight = function(){
            angular.element(document).ready(function(){
                $('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
            });
        };

        $scope.sendMessage = function(conversation){
            ConversationService.sendMessage($scope.message, conversation);
            $scope.message = "";
        };

        $scope.init();

        $scope.openModal = function () {
            console.log(ConversationService.conversations);
            var modalInstance = $modal.open({
              templateUrl: 'editor/modals/convModal.html',
              controller: 'ModalInstanceCtrl',
              resolve: {
                }
            });

            modalInstance.result.then(function (obj) {
                ConversationService.startConversation(obj.selected, obj.name);
            });
        };
    })

    .controller('ModalInstanceCtrl', function ($scope, $modalInstance, $http) {
    $scope.checked = [];
    $scope.name = null;

    $http.get('http://54.77.232.158:9000/users/available').success(function(data) {
        $scope.users = data.online;
      });

    $scope.users = ["testowy1","testowy2","testowy3"];
    $scope.start = function () {
        var obj = {
            selected: $scope.checked,
            name: $scope.name
        }
        $modalInstance.close(obj);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});