'use strict';

angular.module('developerCommunicator.editor', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/editor', {
            templateUrl: 'editor/editor.html',
            controller: 'editorController'
        });
    }])

    .controller('editorController', function ($scope) {
        $scope.conversations = [
            {
                name : 'Conversation 1',
                id : 'conv1',
                active : true,
                code :  'public class Hello111{\n' +
                '\tpublic static void main(String[] args) {\n' +
                '\t\tSystem.out.print("Hello World");\n' +
                '\t}\n' +
                '}\n',
                language : 'java',
                contributors : ['Piotr Brudny', 'Piotr Florczyk', 'Aleksander Maj'],
                chat : [
                    {
                        name : 'Piotr Brudny',
                        avatarInitials : "PB",
                        avatarColor : "ABC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:45'
                    },
                    {
                        name : 'Piotr Florczyk',
                        avatarInitials : "PF",
                        avatarColor : "FBC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:46'
                    },
                    {
                        name : 'Piotr Brudny',
                        avatarInitials : "PB",
                        avatarColor : "ABC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:45'
                    },
                    {
                        name : 'Aleksander Maj',
                        avatarInitials : "AM",
                        avatarColor : "AAA",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:48'
                    },
                    {
                        name : 'Piotr Florczyk',
                        avatarInitials : "PF",
                        avatarColor : "FBC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:52'
                    },
                    {
                        name : 'Piotr Brudny',
                        avatarInitials : "PB",
                        avatarColor : "ABC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:55'
                    },
                    {
                        name : 'Piotr Florczyk',
                        avatarInitials : "PF",
                        avatarColor : "FBC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:58'
                    }
                ]
            },
            {
                name : 'Conversation 2',
                id : 'conv2',
                active : false,
                code :  'public class Hello222{\n' +
                '\tpublic static void main(String[] args) {\n' +
                '\t\tSystem.out.print("Hello World");\n' +
                '\t}\n' +
                '}\n',
                language : 'java',
                contributors : ['Piotr Florczyk', 'Aleksander Maj'],
                chat : [
                    {
                        name : 'Aleksander Maj',
                        avatarInitials : "AM",
                        avatarColor : "AAA",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:48'
                    },
                    {
                        name : 'Piotr Florczyk',
                        avatarInitials : "PF",
                        avatarColor : "FBC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:52'
                    },
                    {
                        name : 'Piotr Brudny',
                        avatarInitials : "PB",
                        avatarColor : "ABC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:55'
                    },
                    {
                        name : 'Piotr Florczyk',
                        avatarInitials : "PF",
                        avatarColor : "FBC",
                        text : 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur bibendum ornare dolor, quis ullamcorper ligula sodales.',
                        time : '14:58'
                    }
                ]
            }
        ];

        $scope.init = function () {
            angular.element(document).ready(function(){
                $('pre code').each(function(i, block) {
                    hljs.highlightBlock(block);
                });
            });
        };

        $scope.init();
    });