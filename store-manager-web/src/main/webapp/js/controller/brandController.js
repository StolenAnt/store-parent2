app.controller('brandController',function ($scope,$controller,brandService) {
    $controller('baseController',{$scope:$scope});
    //取出所有的品牌列表
    $scope.findBrandAll=function () {
        brandService.findBrandAll().success(
            function (response) {
                $scope.brandList=response;
            }
        );
    }


    //分页
    $scope.findPage=function (page,size) {
        brandService.findPage(page,size).success(
            function (response) {
                $scope.brandList=response.rows;
                $scope.paginationConf.totalItems=response.total;
            }
        );
    }


    //新增

    $scope.save=function () {
        var object=null;
        if ($scope.entity.id!=null){
            object=brandService.update($scope.entity);
        }else{
            object=brandService.add($scope.entity);
        }
        object.success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                } else{
                    alert(response.message);
                }
            }
        );
    }
    //取一条
    $scope.findOne=function (id) {
        brandService.findOne(id).success(
            function (response) {
                $scope.entity=response;
            }
        );
    }

    //删除一条
    $scope.deleteOne=function (id) {
        brandService.deleteOne(id).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                } else{
                    alert(response.message);
                }
            }
        );
    }

    //删除多条


    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success){
                    $scope.reloadList();
                    $scope.selectIds=[];
                } else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.searchEntity={};
    $scope.search=function (page,size) {
        brandService.search(page,size,$scope.searchEntity).success(
            function (response) {
                $scope.brandList=response.rows;
                $scope.paginationConf.totalItems=response.total;

            }
        );
    }



});