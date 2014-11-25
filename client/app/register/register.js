'use strict';

angular.module('developerCommunicator.register', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider
                    .when('/register',
                          {
                              templateUrl: 'register/register.html',
                              controller: 'registerController'
                          }
                );
            }])

    .controller('registerController', function ($scope, UserService, $http) {
                    console.log("register");

                    //$scope.login = "maniek";
                    //$scope.password = "maniek123";
                    //
                    //$scope.clickLogin = function () {
                    //    $http.defaults.withCredentials = true;
                    //    $http.post(
                    //        'http://54.77.232.158:9000/users/login',
                    //        {
                    //            username: $scope.login,
                    //            password: $scope.password
                    //        },
                    //        {
                    //            withCredentials: true
                    //        })
                    //        .success(function (data, status, headers, config) {
                    //            UserService.login($scope.login, $scope.password);
                    //            UserService.serverStarted(function () {
                    //                window.location.hash = "/editor";
                    //            });
                    //            $scope.errorOccured = false;
                    //            $scope.errorMessage = '';
                    //        })
                    //        .error(function (data, status, headers, config) {
                    //            console.log("Error when login");
                    //            $scope.errorOccured = true;
                    //            $scope.errorMessage = data.general[0];
                    //        });


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
                    //};

                    $scope.init = function () {

                    };

                    $scope.init();
                });