'use strict';

angular.module('developerCommunicator.login', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'login/login.html',
            controller: 'loginController'
        });
    }])

    .controller('loginController', function ($scope, UserService) {
        $scope.user = "";
        $scope.password = "";
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

        $scope.clickLogin = function(){
            UserService.login($scope.login);
            UserService.serverStarted(function(){
                window.location.hash = "/editor";
            });
        }

        $scope.init();
    });