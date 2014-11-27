'use strict';

angular.module('developerCommunicator.settings', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/settings', {
                    templateUrl: 'settings/settings.html',
                    controller: 'settingsController'
                });
            }])

    .controller('settingsController', function ($scope, $http) {

                    $scope.tadam = function () {
                        console.log("Tadam");
                    };

                    $scope.changePassword = function() {
                        if($scope.new == $scope.new2) {
                            $http.put(
                                'http://54.77.232.158:9000/users/password',
                                {
                                    new: $scope.new,
                                    old: $scope.old
                                })
                                .success(function (data, status, headers, config) {
                                    $scope.new = "";
                                    $scope.new2 = "";
                                    $scope.old = "";
                                })
                                .error(function (data, status, headers, config) {
                                    alert("Error while changing password");
                                });
                        } else {
                            alert("New password must be the same in both fields!");
                        }
                    }
                });