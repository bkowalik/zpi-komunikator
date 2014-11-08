'use strict';

angular.module('developerCommunicator.login', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'login/login.html',
            controller: 'loginController'
        });
    }])

    .controller('loginController', function ($scope) {
        $scope.init = function () {
            $('#toggle').click(function(e) {
                e.preventDefault();
                $('div#form-toggle').toggle('500');
            });

            $('#access').click(function(e) {
                e.preventDefault();
                $('div#form-toggle').toggle('500');
            });
        };

        $scope.init();
    });