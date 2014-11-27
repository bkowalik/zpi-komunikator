'use strict';

angular.module('developerCommunicator.login', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider
                    .when('/login',
                          {
                              templateUrl: 'login/login.html',
                              controller: 'loginController'
                          }
                );
            }])

    .controller('loginController', function ($scope, UserService, $http) {
                    $scope.login = "maniek";
                    $scope.password = "maniek123";

                    $scope.clickLogin = function () {
                        $http.defaults.withCredentials = true;
                        $http.post(
                            'http://54.77.232.158:9000/users/login',
                            {
                                username: $scope.login,
                                password: $scope.password
                            },
                            {
                                withCredentials: true
                            })
                            .success(function (data, status, headers, config) {
                                UserService.login($scope.login, $scope.password);
                                UserService.serverStarted(function () {
                                    window.location.hash = "/editor";
                                });
                                $scope.errorOccured = false;
                                $scope.errorMessage = '';
                            })
                            .error(function (data, status, headers, config) {
                                console.log("Error when login");
                                $scope.errorOccured = true;
                                $scope.errorMessage = data.general[0];
                            });
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
                    };

                    $scope.clickRegister = function () {
                        console.log("clicked");

                        $http.defaults.withCredentials = true;
                        $http.post(
                            'http://54.77.232.158:9000/users/register',
                            {
                                username: $scope.register.login,
                                password: $scope.register.password,
                                email: $scope.register.email
                            },
                            {
                                withCredentials: true
                            })
                            .success(function (data, status, headers, config) {
                                         $scope.errorOccured = false;
                                         $scope.errorMessage = '';
                                         $scope.registerSuccess = true;

                                         $("#register").toggle('500');
                                         $("#login").toggle('500');
                                     })
                            .error(function (data, status, headers, config) {
                                       console.log("Error when login");
                                       $scope.registerSuccess = false;
                                       $scope.errorOccured = true;
                                       $scope.errorMessage = data.general[0];
                                   });
                    };

                    $scope.init = function () {
                        $('#toggle').click(function (e) {
                            e.preventDefault();
                            $('div#form-toggle').toggle('500');
                        });

                        $('#access').click(function (e) {
                            e.preventDefault();
                            $('div#form-toggle').toggle('500');
                        });

                        $('#registerButton').click(function (e) {
                            e.preventDefault();
                            $("#login").toggle('500');
                            $("#register").toggle('500');
                        });

                        $('#backRegisterButton').click(function (e) {
                            e.preventDefault();
                            $("#register").toggle('500');
                            $("#login").toggle('500');
                        });
                    };

                    $scope.init();
                });