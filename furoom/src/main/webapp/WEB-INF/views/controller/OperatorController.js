if (!FuRoomClient) {
	throw "需要EasyService平台远程服务调用JS客户端支持";
}
if (!ejs) {
	throw "需要EasyService平台模板支持";
}  
var userService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IUserService/invoke');
var operationService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IOperationService/invoke');
var productService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IProductService/invoke');

var queryUserByPrivilege=function(privilege){
	var users = userService.findAllByPrivilege(privilege);
	var datasArray = new Array();
	for(var i=0; i<users.size(); i++){
		var d = users.get(i);
		datasArray.push( {id:d.id,name:d.name,sex:d.sex,companyname:d.organizationId,province:d.province,city:d.city})
	}
	return datasArray;
}

var queryProductByState=function(state){
	var products = productService.queryAllByState(state);
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


var OperatorApp = angular.module("operatorApp", ['ngRoute']);

function routeConfig($routeProvider){
    $routeProvider
    .when('/login', {
        controller: 'LoginCtrl',
        templateUrl: 'router/operator/login.html'
    })
    .when('/product',{
    	controller: 'ProductCtrl',
    	templateUrl: 'router/operator/product.html'
    })
    .when('/operation',{
    	controller: 'OperationCtrl',
    	templateUrl: 'router/operator/operation.html'
    })
    .when('/error',{
    	controller: 'ErrorCtrl',
    	templateUrl: 'router/error.html'
    })
    .when('/agent',{
    	controller: 'AgentCtrl',
    	templateUrl: 'router/operator/agent.html'
    })
    .when('/', {
        controller: 'LoginCtrl',
        templateUrl: 'router/operator/login.html'
    })
    .otherwise({
        redirectTo: '/'
    });
};

OperatorApp.config(['$routeProvider', routeConfig]);
OperatorApp.controller('WholeCtrl',function($scope, $location){
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
        title: '信贷经理',
        state:1,
        url: '/agent'
    }, {
        title: '业务管理',
        state:2,
        url: '/operation'
    }, {
        title: '产品管理',
        state:3,
        url: '/product'
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


OperatorApp.controller('ProductCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=30)){
		alert("error user is null!");
		$location.path('/error');
		return;
	}
	$scope.$emit('init', $scope.user);
	var apply = function(){
		$scope.datas = queryProductByState(1);
		$scope.total = $scope.datas.length;
	}
	
	
    $scope.tabs = [{
        title: '待审核',
        state:1,
        url: '../template/operator/pro_apply.html',
        handler : apply
    }
    ];
$scope.datas = null;

$scope.tabs[0].handler();
$scope.currentTab = $scope.tabs[0];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
$scope.propass = function(data){
	try{
		productService.auditProduct(data.id, true);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
$scope.prorefuse=function(data){
	try{
		productService.auditProduct(data.id, false);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
});


OperatorApp.controller('OperationCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=30)){
		alert("error user is null!");
		$location.path('/error');
		return;
	}
	$scope.$emit('init', $scope.user);
	
	
	
	var apply = function(){
		$scope.datas = queryOperationByState(0);
		$scope.total = $scope.datas.length;
	}
	
	
    $scope.tabs = [{
    	title: '待审核',
    	state:0,
    	url: '../template/operator/op_apply.html',
        handler : apply
    }
    ];
$scope.datas = null;

$scope.tabs[0].handler();
$scope.currentTab = $scope.tabs[0];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
$scope.oppass = function(data){
	try{
		operationService.auditOperation(data.id, true);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
$scope.oprefuse=function(data){
	try{
		operationService.auditOperation(data.id, false);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
});


OperatorApp.controller('LoginCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user!=null && ($scope.user.privilege==30)){
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
				 userService.loginOperator($scope.loginid, $scope.password, $scope.picValidate);
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
OperatorApp.controller('AgentCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	if($scope.user==null || ($scope.user.privilege!=30)){
		alert("error user is null!");
		$location.path('/error');
		return;
	}
	$scope.$emit('init', $scope.user);
	var apply = function(){
		$scope.datas = queryUserByPrivilege(10);
		$scope.total = $scope.datas.length;
	}
	
    $scope.tabs = [{
    	title: '待审核',
    	state:0,
    	url: '../template/operator/agent_apply.html',
        handler : apply
    }
    ];
$scope.datas = null;

$scope.tabs[0].handler();
$scope.currentTab = $scope.tabs[0];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(tabUrl) {
    return tabUrl == $scope.currentTab.url;
}
$scope.pass = function(data){
	try{
		userService.auditAgent(data.id, true);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
$scope.refuse = function(data){
	try{
		userService.auditAgent(data.id, false);
		alert('审核完毕');
		apply();
	}catch(e){
		alert(e.message);
	}
}
});
OperatorApp.controller('ErrorCtrl',function($scope, $location){});
