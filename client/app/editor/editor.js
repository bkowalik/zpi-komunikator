'use strict';

angular.module('developerCommunicator.editor', ['ngRoute','ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/editor', {
            templateUrl: 'editor/editor.html',
            controller: 'editorController'
        });
    }])

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
    });


angular.module('developerCommunicator.editor').controller('ModalInstanceCtrl', function ($scope, $modalInstance, $http) {
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