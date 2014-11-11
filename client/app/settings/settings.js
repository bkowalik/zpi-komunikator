'use strict';

angular.module('developerCommunicator.settings', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
                $routeProvider.when('/settings', {
                    templateUrl: 'settings/settings.html',
                    controller: 'settingsController'
                });
            }])

    .controller('settingsController', function () {

                });