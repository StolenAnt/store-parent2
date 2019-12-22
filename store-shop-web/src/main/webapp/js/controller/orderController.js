app.controller('orderController' ,function($scope,$controller,orderService){

    $controller('baseController',{$scope:$scope});//继承
    //读取列表数据绑定到表单中
    $scope.findAll=function(){
        orderService.findAll().success(
            function(response){
                $scope.entity=response;
                alert(entity);
            }
        );
    }

    $scope.updeteStatus=function (id,status) {
        orderService.updeteStatus(id,status).success(
            function (response) {
                alert("成功");
            }
        );
    }

    $scope.viewNum=function (id) {
        alert(id);
    }
});