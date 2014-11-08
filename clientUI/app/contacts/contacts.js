'use strict';

angular.module('developerCommunicator.contacts', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/contacts', {
            templateUrl: 'contacts/contacts.html',
            controller: 'contactsController'
        });
    }])

    .controller('contactsController', function ($scope) {

    });