if (!FuRoomClient) {
	throw "需要EasyService平台远程服务调用JS客户端支持";
}
if (!ejs) {
	throw "需要EasyService平台模板支持";
}  
var userService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IUserService/invoke');
var operationService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IOperationService/invoke');
var productService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IProductService/invoke');

var queryProductByState=function(state){
	var products = productService.queryAllMyProductByState(state);
	var datasArray = new Array();
	for(var i=0; i<products.size(); i++){
		var d = products.get(i);
		datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title, state:d.state,profits:d.profitsArray,requires:d.requiresArray})
	}
	return datasArray;
}

var queryOperationByState=function(state){
	var operations = operationService.findAllOperationByAgentAndStateAndType(state, -1);
	var datasArray = new Array();
	for(var i=0; i<operations.size(); i++){
		var d = operations.get(i);
		datasArray.push( {id:d.id,productname:"产品名", amount:d.amount,period:d.period,loanType:d.loanType})
	}
	return datasArray;
}


var AgentApp = angular.module("agentApp", ['ngRoute']);

function routeConfig($routeProvider){
    $routeProvider
    .when('/login', {
        controller: 'LoginCtrl',
        templateUrl: 'router/agent/login.html'
    })
    .when('/register',{
    	controller: 'RegisterCtrl',
    	templateUrl: 'router/agent/register.html'
    })
    .when('/product',{
    	controller: 'ProductCtrl',
    	templateUrl: 'router/agent/product.html'
    })
    .when('/operation',{
    	controller: 'OperationCtrl',
    	templateUrl: 'router/agent/operation.html'
    })
    .when('/error',{
    	controller: 'ErrorCtrl',
    	templateUrl: 'router/error.html'
    })
    .when('/detail/:isnew/:productId', {
        controller: 'DetailCtrl',
        templateUrl: 'router/agent/productDetail.html'
    })
    .when('/relate', {
        controller: 'RelateCtrl',
        templateUrl: 'router/agent/relateToProduct.html'
    })
    .when('/', {
        controller: 'RegisterCtrl',
        templateUrl: 'router/agent/register.html'
    })
    .otherwise({
        redirectTo: '/'
    });
};

AgentApp.config(['$routeProvider', routeConfig]);
AgentApp.controller('WholeCtrl',function($scope, $location){
	$scope.user=null;
	
	$scope.usercontent='';
	$scope.a_display='block';
	$scope.c_display='none';
	
	$scope.$on('login', function(event, username){
		$scope.user=userService.getCurrentUser();
		if($scope.user!=null)
			{
				$scope.usercontent='您好：'+$scope.user.name;
				$scope.a_display='none';
				$scope.c_display='block';
			}else{
				$scope.usercontent='';
				$scope.a_display='block';
				$scope.c_display='none';
			}
	});
	$scope.$on('init',function(event, user){
		$scope.user = user;
		if($scope.user!=null)
		{
			$scope.usercontent='您好：'+$scope.user.name;
			$scope.a_display='none';
			$scope.c_display='block';
		}else{
			$scope.usercontent='';
			$scope.a_display='block';
			$scope.c_display='none';
		}
	});
	$scope.logout = function(){
		userService.loginOut();
		var path = $location.path();
		$location.path(path);
	}
	
	
	
	
	$scope.tabs = [{
        title: '产品管理',
        state:1,
        url: '/product'
    }, {
        title: '业务管理',
        state:2,
        url: '/operation'
    }
    ];

$scope.currentTab = $scope.tabs[0];


$scope.onClickTab = function (tab) {
    $scope.currentTab = tab;
    $location.path(tab.url);
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
});

AgentApp.controller('RegisterCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	
	if($scope.user!=null && ($scope.user.privilege==10||$scope.user.privilege==11)){
		$location.path('/product');
	}
	$scope.$emit('init', $scope.user);
		$scope.provinces = provinces;
			
		$scope.currentProvince = $scope.provinces[0];
		
		var organizations = [
		                     {name:"清华大学", id:1},
		                     {name:"北京大学", id:2}
		                     ];
		
		$scope.organizations = organizations;
		
		 $scope.user = {
				  name:"",
				  sex:"man",
				  picValidate:"",
				  tel:"",
				  telValidate:"",
				  organization:$scope.organizations[0],
				  province:$scope.currentProvince,
				  city:$scope.currentProvince.citys[0]
		  }
			$scope.provinceChange = function(x){
			  $scope.currentProvince = x;
			  $scope.user.province = x;
			  $scope.user.city = x.citys[0];
		  }
		 $scope.register = function(){
			 if ($scope.userForm.$valid) {
		    	  var user = {"_t_":"fsm.model.User","name":$scope.user.name, "loginId":$scope.user.tel, "password" : "111111", "tel" : $scope.user.tel, "sex" : $scope.user.sex, "province" : $scope.user.province.code, "city" : $scope.user.city.code, "organizationId" : $scope.user.organization.id, "privilege" : 10};
		    	  try{
		    		  userService.registerInner(user);
		    	  }catch(e){
		    		  alert(e.message);
		    		  return;
		    	  }
		    	  $location.path('/product');
		        } else {
		          $scope.userForm.submitted = true;
		          alert('参数填写错误！');
		        }
		 }
});

AgentApp.controller('ProductCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=10&&$scope.user.privilege!=11)){
		alert("error user is null!");
		$location.path('/error');
		return;
	}
	$scope.$emit('init', $scope.user);
	var init = function(){
		$scope.datas = queryProductByState(0);
		$scope.total = $scope.datas.length;
	}
	var apply = function(){
		$scope.datas = queryProductByState(1);
		$scope.total = $scope.datas.length;
	}
	var pass = function(){
		$scope.datas = queryProductByState(2);
		$scope.total = $scope.datas.length;
	}
	var refuse = function(){
		$scope.datas = queryProductByState(3);
		$scope.total = $scope.datas.length;
	}
	var invalid = function(){
		$scope.datas = queryProductByState(4);
		$scope.total = $scope.datas.length;
	}
	
	
    $scope.tabs = [{
    	title: '创建中',
    	state:0,
    	url: '../template/agent/init.html',
        handler : init
    }, {
        title: '申请审核',
        state:1,
        url: '../template/agent/apply.html',
        handler : apply
    }, {
        title: '审核通过',
        state:2,
        url: '../template/agent/pass.html',
        handler : pass
    },{
        title: '审核拒绝',
        state: 3,
        url: '../template/agent/refuse.html',
        handler : refuse
    },{
        title: '失效',
        state: 4,
        url: '../template/agent/invalid.html',
        handler : invalid
    }
    ];
$scope.datas = null;

$scope.tabs[1].handler();
$scope.currentTab = $scope.tabs[1];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
$scope.edit = function(productId){
	$location.path('/detail/0/'+productId);
}
$scope.apply = function(productId){
	try{
		productService.applyAuditProduct(productId);
		alert('申请审核成功');
		$scope.onClickTab($scope.tabs[0]);
	}catch(e){
		alert(e.message);
	}
}

$scope.addproduct = function(){
	if($scope.user.privilege==11)
	{
		$location.path('/relate');
	}else{
		alert('您尚未通过审核，无法添加产品！');
	}
}
});


AgentApp.controller('OperationCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=10&&$scope.user.privilege!=11&&$scope.user.privilege!=12)){
		alert("error user is null!");
		$location.path('/error');
		return;
	}
	$scope.$emit('init', $scope.user);
	
	
	
	var apply = function(){
		$scope.datas = queryOperationByState(1);
		$scope.total = $scope.datas.length;
	}
	var handling = function(){
		$scope.datas = queryOperationByState(3);
		$scope.total = $scope.datas.length;
	}
	var complete = function(){
		$scope.datas = queryOperationByState(4);
		$scope.total = $scope.datas.length;
	}
	var fail = function(){
		$scope.datas = queryOperationByState(5);
		$scope.total = $scope.datas.length;
	}
	
	
    $scope.tabs = [{
    	title: '申请中',
    	state:0,
    	url: '../template/agent/op_apply.html',
        handler : apply
    }, {
        title: '处理中',
        state:1,
        url: '../template/agent/op_handling.html',
        handler : handling
    }, {
        title: '已完成',
        state:2,
        url: '../template/agent/op_complete.html',
        handler : complete
    },{
        title: '失败',
        state: 3,
        url: '../template/agent/op_fail.html',
        handler : fail
    }
    ];
$scope.datas = null;

$scope.tabs[1].handler();
$scope.currentTab = $scope.tabs[1];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
$scope.edit = function(productId){
	$location.path('/detail/0/'+productId);
}
$scope.apply = function(productId){
	try{
		productService.applyAuditProduct(productId);
		alert('申请审核成功');
		$scope.onClickTab($scope.tabs[0]);
	}catch(e){
		alert(e.message);
	}
}

$scope.addproduct = function(){
	$location.path('/relate');
}
});


AgentApp.controller('LoginCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user!=null && ($scope.user.privilege==10||$scope.user.privilege==11)){
		$location.path('/product');
	}
	$scope.$emit('init', $scope.user);
	 $scope.loginid="";
	 $scope.password="";
	 $scope.picValidate="";
	 var path = $location.path();
	 $scope.validateUrl="/login/graphValidateCode?t="+Math.random();
	 $scope.refresh = function(){
		 $scope.validateUrl="/login/graphValidateCode?t="+Math.random();
	 }
	 $scope.login = function(){
		 if ($scope.loginForm.$valid) {
			 try{
				 userService.loginAgent($scope.loginid, $scope.password, $scope.picValidate);
				 $scope.$emit('login', 'wd');
				 $location.path('/myaccount');
			 }catch(e){
				 alert(e.message);
			 }
		 }else {
	          alert('参数填写错误！');
	        }
	 }
});

AgentApp.controller('ErrorCtrl',function($scope, $location){});
AgentApp.controller('RelateCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=10&&$scope.user.privilege!=11&&$scope.user.privilege!=12)){
		alert("error user is null!");
		$location.path('/error');
	}
	$scope.$emit('init', $scope.user);
	
	try{
		var products = productService.queryAllByOrgAndCityAndState($scope.user.organizationId, $scope.user.city, 2);
		var datasArray = new Array();
		for(var i=0; i<products.size(); i++){
			var d = products.get(i);
			datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title, city:d.cityCode, related:(d.related==1?"已关联":"未关联"), state:d.state,profits:d.profitsArray,requires:d.requiresArray})
		}
		$scope.datas = datasArray;
		$scope.total = products.size();
	}catch(e){
		alert(e.message);
	}
	$scope.changeRelation = function(product){
		if(product.related=='已关联'){
			productService.removeRelationTo(product.id);
			product.related='未关联';
		}else{
			productService.addRelationTo(product.id);
			product.related='已关联';
		}
	}
	$scope.createproduct = function(){
		$location.path('/detail/1/0');
	}
});
AgentApp.controller('DetailCtrl',function($scope, $location, $routeParams){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=10&&$scope.user.privilege!=11)){
		alert("error user is null!");
		$location.path('/error');
	}
	$scope.$emit('init', $scope.user);
	$scope.isnew = $routeParams.isnew;
	$scope.types = loanTypes;
	$scope.productId = null;
	if($scope.isnew==='1'){
		$scope.product = {
				title : "",
				type : $scope.types[0],
				profits : "",
				requires : ""
		}
	}else{
		$scope.productId = parseInt($routeParams.productId);
		var product = productService.find($scope.productId);
		var currentType = null;
		for(var i=0; i<$scope.types.length; i++){
			var type = $scope.types[i];
			if(type.type===product.loanType){
				currentType = type;
			}
		}
		
		$scope.product = {
				title : product.title,
				type : currentType,
				profits : product.profits,
				requires : product.requires
		}
		
		var actionArray = new Array();
		for(var i=0; i<product.actions.size(); i++){
			actionArray.push(product.actions.get(i));
		}
		
		$scope.actions = actionArray;
	}
		
		$scope.action = {
				ind : 0,
				title : "",
				description : "",
				estimatedTime : 0
		}
		
		$scope.confirm = function(){	
			if ($scope.productForm.$valid) {
				try{
					if($scope.isnew=='1' && $scope.productId==null)
					{
						$scope.productId = productService.createProduct($scope.product.title, $scope.product.type.type, $scope.product.profits, $scope.product.requires);
						alert('产品创建成功');
						$location.path('/detail/0/'+$scope.productId);
					}else{
						productService.updateProduct($scope.productId, $scope.product.title, $scope.product.type.type, $scope.product.profits, $scope.product.requires);
						alert('产品修改成功');
					}
					
				}catch(e){
					alert(e.message);
					return;
				}
			}else {
		          $scope.productForm.submitted = true;
		          alert('参数填写错误！');
		        }
		}
		
		
		$scope.add = function(){
			
			if($scope.productId==null){
				alert('请先添加产品基本信息！');
				return;
			}
			
			if ($scope.actionForm.$valid) {
		    	  try{
		    		  productService.addProductAction($scope.action.ind, $scope.productId, $scope.action.title, $scope.action.description, $scope.action.estimatedTime)
		    	  }catch(e){
		    		  alert(e.message);
		    		  return;
		    	  }
		    	  
		    	var product = productService.find($scope.productId);
		  		var actionArray = new Array();
		  		for(var i=0; i<product.actions.size(); i++){
		  			actionArray.push(product.actions.get(i));
		  		}
		  		
		  		$scope.actions = actionArray;
		  		$scope.action = {
		  				ind : 0,
		  				productId : $scope.productId,
		  				title : "",
		  				description : "",
		  				estimatedTime : 0
		  		}
		    	  
		    	  
		        } else {
		          $scope.actionForm.submitted = true;
		          alert('参数填写错误！');
		        }
		}
});
