'use strict';

angular.module('developerCommunicator.contacts', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/contacts', {
            templateUrl: 'contacts/contacts.html',
            controller: 'contactsController'
        });
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
    });