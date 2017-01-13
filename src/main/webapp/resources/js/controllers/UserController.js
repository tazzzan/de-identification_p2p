App.controller('UserController', function($scope, $http) {
	
	$scope.user={};

    /**
     * AJAX
     */

    $scope.fetchUsersList = function() {
        $http.get('users/userlist.json').success(function(userList){
            $scope.users = userList;
        });
    };

    $scope.addNewUser = function(user) {
        $http.post('users/add/', user).success(function() {
            $scope.fetchUsersList();
        });
        $scope.user = '';
    };

    $scope.removeUser = function(id) {
        $http.delete('users/remove/' + id).success(function() {
            $scope.fetchUsersList();
        });
    };

    $scope.removeAllUsers = function() {
        $http.delete('users/removeAll').success(function() {
            $scope.fetchUsersList();
        });

    };

    $scope.fetchUsersList();
});
