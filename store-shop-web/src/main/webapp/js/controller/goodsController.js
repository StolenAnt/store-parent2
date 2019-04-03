 //控制层 
app.controller('goodsController' ,function($scope,$controller   ,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}

    $scope.add=function(){
		$scope.entity.goodsDesc.introduction=editor.html();//
        goodsService.add( $scope.entity  ).success(
            function(response){
                if(response.success){
                    alert("录入成功");
                    $scope.entity={};
                    editor.html("");
                }else{
                    alert(response.message);
                }
            }
        );
    }


	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}


	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.uploadFile=function () {
		uploadService.uploadFile().success(
			function (response) {
				if (response.success){
					$scope.image_entity.url=response.message;
				} else{
					alert(response.message);
				}
            }
		);
    }

    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]} };
    $scope.add_image_entity=function () {
		$scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function (index) {
		$scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    //分类列表
	 $scope.selectIteamCat1List=function () {
		 itemCatService.findByParentId(0).success(
		 	function (response) {
				$scope.iteamCat1List=response;
            }
		 );
     }

    // 查询二级
     $scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
         itemCatService.findByParentId(newValue).success(
             function (response) {
                 $scope.iteamCat2List=response;
             }
         );
     });

    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.iteamCat3List=response;
            }
        );
    });

    //读取模板ID
    $scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) {
                $scope.entity.goods.typeTemplateId=response.typeId;
            }
        );
    });

    //读取品牌 拓展属性 规格列表
    $scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {
        typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate=response;//模板对象
                // alert($scope.typeTemplate.brandIds);
                //品牌列表
                $scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);//品牌类型转换
                //拓展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);

            }
        );


        //规格列表
        typeTemplateService.findSpecList(newValue).success(
            function (response) {
                $scope.specList=response;

            }
        );
    });



    //选择规格内容
    $scope.updateSpecAttribute=function ($event,name,value) {
        var object=$scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);

        if (object!=null){
            if($event.target.checked){
                object.attributeValue.push(value);
            }else{
                //取消勾选
                var index=object.attributeValue.indexOf(value);
                object.attributeValue.splice(index ,1);
                if (object.attributeValue.length==0){//x选项取消整条记录取消
                    var index2=$scope.entity.goodsDesc.specificationItems.indexOf(object);
                    $scope.entity.goodsDesc.specificationItems.splice(index2,1);
                }
            }


        } else{
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
        }
    }


    //创建SKU列表

    $scope.createItemList=function () {
        $scope.entity.itemList=[{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];//列表初始化

       var items= $scope.entity.goodsDesc.specificationItems;
       for (var i=0;i<items.length;i++){
           $scope.entity.itemList= addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
       }
    }

    addColumn=function (list,columnName,columnValues) {
        var newList=[];
        for (var i=0;i<list.length;i++){
            var oldRow=list[i];
            for (var j=0;j<columnValues.length;j++){
                var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
            }
        }

        return newList;
    }

    $scope.status=['未审核','已审核','审核未通过','已关闭'];

    $scope.itemCatList=[];
    //分类列表

    $scope.findIteamCatList=function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
                }
            }
        );
    }

});	
