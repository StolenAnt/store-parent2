app.controller('cartController',function($scope,$http,cartService,loginService,addressService){


    $scope.findCartList=function(){
        cartService.findCartList().success(
            function(response){
                $scope.cartList=response;
                $scope.totalValue= cartService.sum($scope.cartList);
            }
        );
    }

    //数量加减
    $scope.addGoodsToCartList=function(itemId,num){
        cartService.addGoodsToCartList(itemId,num).success(
            function(response){
                if(response.success){//如果成功
                    $scope.findCartList();//刷新列表
                }else{
                    alert(response.message);
                }
            }
        );
    }

    // sum=function(){
    //     $scope.totalNum=0;//总数量
    //     $scope.totalMoney=0;//总金额
    //
    //     for(var i=0;i<$scope.cartList.length ;i++){
    //         var cart=$scope.cartList[i];//购物车对象
    //         for(var j=0;j<cart.orderItemList.length;j++){
    //             var orderItem=  cart.orderItemList[j];//购物车明细
    //             $scope.totalNum+=orderItem.num;//累加数量
    //             $scope.totalMoney+=orderItem.totalFee;//累加金额
    //         }
    //     }
    // }

    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

    $scope.dele=function (itemId) {
       cartService.deleteCartList(itemId).success(
           function (response) {
               $scope.cartList=response;
           }
       );

    }

    //获取当前用户的列表
    $scope.findAddressList=function () {
        cartService.findAddressList().success(
            function (response) {
                $scope.addressList=response;
                // $scope.entity=[];
                for (var i=0;i<$scope.addressList.length;i++){
                    if ($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                        break;
                    }
                }
            }
        );
    }

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;

    }
    //判断某地址对象是不是当前选择的地址
    $scope.isSelectedAddress=function (address) {
        if (address==$scope.address){
            return true;
        } else{
            return false;
        }
    }

    //保存

    $scope.save=function(){
             $scope.entity.address=$scope.address1;
             $scope.entity.provinceId=$scope.provinceId.province;
             $scope.entity.cityId=$scope.cityId.city;
             $scope.entity.townId=$scope.townId.area;
            addressService.add( $scope.entity  ).success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.findAddressList();//重新加载
                    $scope.entity=[];
                    $scope.address1="";
                    $scope.provinceId=[];
                    $scope.cityId=[];
                    $scope.townId=[];

                }else{
                    alert(response.message);
                }
            }
        );
    }

    //支付类型
    $scope.order={paymentType:'1'}

    $scope.selectPayType=function (type) {
        $scope.order.paymentType=type;
    }

    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;
        $scope.order.receiverMobile=$scope.address.mobile;
        $scope.order.receiver=$scope.address.contact;

        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success){
                    //页面跳转
                    if ($scope.order.paymentType=='1'){//是否是支付宝支付
                        location.href="/pay/ZhiFuBao.do"
                        //$http.get('/pay/ZhiFuBao.do');

                    } else{
                        location.href="paysuccess.html"
                    }
                } else{
                    alert(response.message);
                }
            }
        );
    }

    $scope.address1="";
    $scope.findProvinces=function () {
        addressService.findProvinces().success(
            function (response) {
                $scope.provinceList=response;
            }
        );
    }

    // 查询二级
    $scope.$watch('provinceId',function (newValue,oldValue) {
        $scope.address1=newValue.province;
        addressService.findCity(newValue.provinceid).success(
            function (response) {
                $scope.cityList=response;
            }
        );
    });
    // 查询三级
    $scope.$watch('cityId',function (newValue,oldValue) {
        $scope.address1=$scope.address1+newValue.city;
        addressService.findAreas(newValue.cityid).success(
            function (response) {
                $scope.AreasList=response;
            }
        );
    });
    // 查询四级
    $scope.$watch('townId',function (newValue,oldValue) {
        $scope.address1=$scope.address1+newValue.area;
    });

});