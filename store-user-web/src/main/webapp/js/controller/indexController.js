//首页控制器
app.controller('indexController',function($scope,loginService,orderService){
	$scope.showName=function(){
			loginService.showName().success(
					function(response){
						$scope.loginName=response.loginName;
					}
			);
	}

	$scope.OrderGroup=[{'orderItemsList':'','orderList':''}]
    $scope.findOrderListByUser=function () {
        orderService.findOrderListByUser().success(
            function (response) {
                $scope.entity=response;
                alert(entity.orderItemsList);

            }
        );
    }
});