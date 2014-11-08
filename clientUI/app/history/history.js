'use strict';

angular.module('developerCommunicator.history', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/history', {
            templateUrl: 'history/history.html',
            controller: 'historyController'
        });
    }])

    .controller('historyController', function ($scope) {

    });