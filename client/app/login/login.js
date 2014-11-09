'use strict';

angular.module('developerCommunicator.login', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'login/login.html',
            controller: 'loginController'
        });
    }])

    .controller('loginController', function ($scope, UserService, $http) {
        $scope.login = "testowy";
        $scope.password = "testowy";
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
            // $http.defaults.withCredentials = true;
            // $http.post('http://54.77.232.158:9000/users/login',{username: $scope.login,password:$scope.password},{withCredentials: true})
            //      .success(function(data, status, headers, config) {
            //         console.log(headers);
            //         console.log($cookies);
                     UserService.login($scope.login,$scope.password);
                     UserService.serverStarted(function(){
                         window.location.hash = "/editor";
                     });
            //       }).error(function(data, status, headers, config){
            //         console.log("Error when login");
            //       });
            // $.ajax({
            //   type: "POST",
            //   url: "http://54.77.232.158:9000/users/login",
            //   data: {username: $scope.login,password:$scope.password},
            //   success: function(data, status, xhr){
            //     console.log(data);
            //     console.log(status);
            //     console.log(xhr);
            //   }
            // });
        }
        $scope.init();
    });