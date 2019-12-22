 //控制层 
app.controller('itemController' ,function($scope,$http){
	
    $scope.specificationItems={};//存储用户选择的规格
    
    $scope.addNum=function (x) {
        if (x>0) {
            $scope.num+=x;
            $scope.sku.price=$scope.price+$scope.sku.price;
        }
        if (x<0) {
            $scope.num+=x;
            $scope.sku.price-=$scope.price;
        }
        if ($scope.num<1) {
            $scope.num=1;
            $scope.sku.price=$scope.price;
        }
    }

    $scope.selectSpecification=function (key,value) {
        $scope.specificationItems[key]=value;//用户选择规格
        $scope.num=1;
        searchSku();
        $scope.price=$scope.sku.price;
    }

    $scope.isSelected=function(key,value) {
        if ($scope.specificationItems[key]==value) {
            return true;
        }else{
            return false;
        }
    }

    $scope.sku={};//当前选择SKU

    //加载默认SKU
    $scope.loadSku=function(){
        $scope.sku=skuList[0];
        $scope.price=$scope.sku.price;
        $scope.specificationItems=JSON.parse(JSON.stringify($scope.sku.spec));
    }
    //匹配两个对象是否相等
    mathObject=function(map1,map2){
        for (var k in map1) {
            if (map1[k]!=map2[k]) {
                return false;
            }
        }
        for (var k in map2) {
            if (map1[k]!=map2[k]) {
                return false;
            }
        }
        return true;
    }

    //更具规格查找SKU
    searchSku=function(){
        for (var i = 0; i < skuList.length; i++) {
           if(mathObject(skuList[i].spec,$scope.specificationItems) ) {
                $scope.sku=angular.copy(skuList[i]);
                    return ;
           }
        }
        $scope.sku={id:0,title:'----',price:0};
    }

    //添加商品到购物车
    $scope.addToCart=function(){
        //alert('SKUID:'+$scope.sku.id);
            $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='
                +$scope.sku.id+'&num='+$scope.num ,{'withCredentials':true} ).success(
                    function(response){
                        if(response.success){
                            location.href='http://localhost:9107/cart.html';                        
                        }else{
                            alert(response.message);
                        }                   
                    }                       
                );  
    }

});	
