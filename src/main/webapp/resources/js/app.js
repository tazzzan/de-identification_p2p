


var AngularSpringApp = {};

//angular.module('MyApp',['ngMessages', 'material.svgAssetsCache']);

var App = angular.module('Deidentification', ['AngularSpringApp.filters', 'AngularSpringApp.services', 'AngularSpringApp.directives']);

// Declare app level module which depends on filters, and services
App.config(['$routeProvider', function ($routeProvider) {
   
    $routeProvider.when('/users', {
    	templateUrl: 'users/layout'
    });
    
    $routeProvider.when('/overview', {
    	templateUrl: 'overview/layout'
    });

    $routeProvider.otherwise({redirectTo: '/overview'});
}]);

App.directive('tabIndex', function () {
    return {
        restrict: 'A',
        scope: {
            indextab: '@'
        },
        link: function (scope, $scope) {

            $scope.datasetTab = scope.indextab;

        }
    }
});


