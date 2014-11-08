'use strict';

// Declare app level module which depends on views, and components
angular.module('developerCommunicator', [
    'ngRoute',
    'developerCommunicator.editor',
    'developerCommunicator.contacts',
    'developerCommunicator.settings',
    'developerCommunicator.history',
    'developerCommunicator.login'
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .otherwise({redirectTo: '/editor'});
    }]);

