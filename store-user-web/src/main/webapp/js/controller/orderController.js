app.controller('orderController',function($scope,orderService){



    $scope.findOrderListByUser=function () {
        orderService.findOrderListByUser().success(
            function (response) {
                $scope.entity=response;
            }
        );
    }
});