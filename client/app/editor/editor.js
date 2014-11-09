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

    .controller('contactsController', function ($scope) {
        $scope.contacts = [
            {
                "name": {"title": "miss", "first": "violet", "last": "hernandez"},
                "location": "D12",
                "email": "violet.hernandez61@example.com",
                "username": "organicpanda958",
                "phone": "(540)-841-4132",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/women/76.jpg",
                }
            },
            {
                "name": {"title": "mrs", "first": "harper", "last": "medina"},
                "location": "D34",
                "email": "harper.medina98@example.com",
                "username": "bigpanda573",
                "phone": "(681)-715-2202",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/women/30.jpg",
                }
            },
            {
                "name": {"title": "mr", "first": "nathaniel", "last": "baker"},
                "location": "B45",
                "email": "nathaniel.baker87@example.com",
                "phone": "(304)-407-6713",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/men/0.jpg",
                }
            },
            {
                "name": {"title": "mr", "first": "liam", "last": "robinson"},
                "location": "J56",
                "email": "liam.robinson47@example.com",
                "phone": "(803)-333-2499",
                "picture": {
                    "large": "http://api.randomuser.me/portraits/men/33.jpg",
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

    .controller('editorController', function ($scope, $modal, UserService) {
        $scope.conversations = [
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
        ];

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

        $scope.startConversation = function(users, convName){
            $scope.conversations.push({
                name: convName,
                id : _.uniqueId('conv_'),
                code :  'public class Hello222{\n' +
                '\tpublic static void main(String[] args) {\n' +
                '\t\tSystem.out.print("Hello World");\n' +
                '\t}\n' +
                '}\n',
                language : 'java',
                contributors : users
            });
            $scope.reloadHighlight();

            UserService.addMessageListener(users, function(resp){
                console.log(resp);
            });
        }

        $scope.init();

        $scope.openModal = function () {
            var modalInstance = $modal.open({
              templateUrl: 'editor/modals/convModal.html',
              controller: 'ModalInstanceCtrl',
              resolve: {
                }
            });

            modalInstance.result.then(function (selected, name) {
                $scope.startConversation(selected, name);
            });
        };
    })

    .controller('ModalInstanceCtrl', function ($scope, $modalInstance, $http) {
    $scope.checked = [];
    $scope.name = "";

    $http.get('http://54.77.232.158:9000/users/available').success(function(data) {
        $scope.users = data.online;
      });

    $scope.start = function () {
        $modalInstance.close($scope.checked,$scope.name);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});