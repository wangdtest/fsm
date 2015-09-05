if (!FuRoomClient) {
	throw "需要EasyService平台远程服务调用JS客户端支持";
}
if (!ejs) {
	throw "需要EasyService平台模板支持";
}  
var userService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IUserService/invoke');
var operationService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IOperationService/invoke');
var productService = FuRoomClient.getRemoteProxy('/furoom/fsm.service.IProductService/invoke');

var UserApp = angular.module("userApp", ['ngRoute']);

function routeConfig($routeProvider){
    $routeProvider
    .when('/select/:operationId', {
        controller: 'SelectCtrl',
        templateUrl: 'router/user/select.html'
    })
    .when('/first',{
    	controller: 'FirstStepCtrl',
    	templateUrl: 'router/user/first.html'
    })
    .when('/second/:type/:amount/:period/:province/:city',{
    	controller: 'UserCtrl',
    	templateUrl: 'router/user/second.html'
    })
    .when('/error',{
    	controller: 'ErrorCtrl',
    	templateUrl: 'router/error.html'
    })
    .when('/login', {
        controller: 'LoginCtrl',
        templateUrl: 'router/user/login.html'
    })
    .when('/detail/:productId/:operationId', {
        controller: 'DetailCtrl',
        templateUrl: 'router/user/productDetail.html'
    })
    .when('/', {
        controller: 'FirstStepCtrl',
        templateUrl: 'router/user/first.html'
    })
    .otherwise({
        redirectTo: '/'
    });
};

UserApp.config(['$routeProvider', routeConfig]);
UserApp.controller('WholeCtrl',function($scope, $location){
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
});
UserApp.controller('FirstStepCtrl',function($scope, $location){
	$scope.user = userService.getCurrentUser();
	$scope.$emit('init', $scope.user);
	$scope.types = loanTypes;
	
	$scope.amounts = amounts;
	
	$scope.periods = periods;
	
	 	$scope.operation = {
				amount:$scope.amounts[1],
				period:$scope.periods[2],
				type:$scope.types[0]
	 };
	 	

		$scope.provinces = provinces;
			
			$scope.currentProvince = $scope.provinces[0];
		
			$scope.operation = {
				amount:$scope.amounts[1],
				period:$scope.periods[2],
				type:$scope.types[0],
				province:$scope.currentProvince,
				city:$scope.currentProvince.citys[0]
			};
			$scope.provinceChange = function(x){
			  $scope.currentProvince = x;
			  $scope.operation.province = x;
			  $scope.operation.city = x.citys[0];
		  }
		 $scope.apply = function(){
			 if($scope.user==null){
				 $location.path('/second/'+$scope.operation.type.type+"/"+$scope.operation.amount.amount+'/'+$scope.operation.period.period+'/'+$scope.operation.province.code+'/'+$scope.operation.city.code);
			 }else{
				 var operationId = null;
		    	  try{
		    		  operationId = operationService.applyOperation(parseInt($scope.operation.type.type), parseInt($scope.operation.amount.amount), parseInt($scope.operation.period.period), $scope.operation.province.code, $scope.operation.city.code);
		    	  }catch(e){
		    		  alert(e.message);
		    		  return;
		    	  }
		    	  $location.path('/select/'+operationId);
			 }
		};
});

UserApp.controller('ErrorCtrl', function($scope, $location){
	
})

UserApp.controller('DetailCtrl', function($scope, $location, $routeParams){
	var productId = parseInt($routeParams.productId);
	var operationId = parseInt($routeParams.operationId);
	$scope.product = productService.find(productId);
	$scope.operation = operationService.find(operationId);
	var agentArray = new Array();
	var res = productService.queryAllAgentsByProduct(productId);
	for(var i=0; i<res.size(); i++){
		agentArray.push(res.get(i));
	}
	$scope.agents = agentArray;
	$scope.amountName = '--';
	for(var i=0; i<amounts.length; i++){
		var amount = amounts[i];
		if(amount.amount==$scope.operation.amount){
			$scope.amountName = amount.name;
			break;
		}
	}
	$scope.periodName = '--';
	for(var i=0; i<periods.length; i++){
		var period = periods[i];
		if(period.period==$scope.operation.period){
			$scope.periodName = period.name;
			break;
		}
	}
	$scope.bind = function(agentId){
		try{
			operationService.bindAgentAndProductToOperation(operationId, agentId, productId);
			window.location.href='myaccount.html';
		}catch(e){
			alert(e.message);
		}
	}
})

UserApp.controller('SelectCtrl', function($scope,$location, $routeParams){
	$scope.user = userService.getCurrentUser();
	$scope.$emit('init', $scope.user);
	
	if($scope.user==null){
		alert("error user is null!");
		$location.path('/error');
	}
	var operationId = parseInt($routeParams.operationId);
	var operation = operationService.find(operationId);
	if(operation==null){
		alert("error operation is null!");
		$location.path('/error');
	}
	
	$scope.loanTypeName = "--";
	for(var i=0; i<loanTypes.length; i++){
		var loanType = loanTypes[i];
		if(loanType.type==operation.loanType){
			$scope.loanTypeName = loanType.name;
			break;
		}
	}
	$scope.provinceName = '--';
	var currentProvince;
	for(var i=0; i<provinces.length; i++){
		var province = provinces[i];
		if(province.code==operation.province){
			$scope.provinceName = province.name;
			currentProvince = province;
			break;
		}
	}
	$scope.cityName = '--';
	for(var i=0; i<currentProvince.citys.length; i++){
		var city = currentProvince.citys[i];
		if(city.code==operation.city){
			$scope.cityName = city.name;
			break;
		}
	}
	$scope.amountName = '--';
	for(var i=0; i<amounts.length; i++){
		var amount = amounts[i];
		if(amount.amount==operation.amount){
			$scope.amountName = amount.name;
			break;
		}
	}
	$scope.periodName = '--';
	for(var i=0; i<periods.length; i++){
		var period = periods[i];
		if(period.period==operation.period){
			$scope.periodName = period.name;
			break;
		}
	}
	
	var successrate = function(){
		var res = productService.queryAllByTypeAndCityAndStateAndOrder(operation.loanType, operation.city, 2, 1, 0, 10);
		var ds = res.get("result");
		var datasArray = new Array();
		if(ds!=null){
		for(var i=0; i<ds.size(); i++){
			var d = ds.get(i);
			datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title,profits:d.profitsArray,requires:d.requiresArray})
		}
		}
		
		$scope.datas = datasArray;
		$scope.total = res.get("total");
	}
	var lessdate = function(){
		var res = productService.queryAllByTypeAndCityAndStateAndOrder(operation.loanType, operation.city, 2, 2, 0, 10);
		var ds = res.get("result");
		var datasArray = new Array();
		if(ds!=null){
		for(var i=0; i<ds.size(); i++){
			var d = ds.get(i);
			datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title,profits:d.profitsArray,requires:d.requiresArray})
		}
		}
		
		$scope.datas = datasArray;
		$scope.total = res.get("total");
	}
	var lessstep = function(){
		var res = productService.queryAllByTypeAndCityAndStateAndOrder(operation.loanType, operation.city, 2, 3, 0, 10);
		var ds = res.get("result");
		var datasArray = new Array();
		if(ds!=null){
		for(var i=0; i<ds.size(); i++){
			var d = ds.get(i);
			datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title,profits:d.profitsArray,requires:d.requiresArray})
		}
		}
		
		$scope.datas = datasArray;
		$scope.total = res.get("total");
	}
	var lessfee = function(){
		var res = productService.queryAllByTypeAndCityAndStateAndOrder(operation.loanType, operation.city, 2, 0, 0, 10);
		var ds = res.get("result");
		var datasArray = new Array();
		if(ds!=null){
		for(var i=0; i<ds.size(); i++){
			var d = ds.get(i);
			datasArray.push( {id:d.id,companyname:d.companyname,productname:d.title,profits:d.profitsArray,requires:d.requiresArray})
		}
		}
		
		$scope.datas = datasArray;
		$scope.total = res.get("total");
	}
	
	
    $scope.tabs = [{
    	title: '最少费用',
    	state:1,
    	url: 'template/user/productList.html',
        handler : lessfee
    }, {
        title: '最短日期',
        state:2,
        url: 'template/user/productList.html',
        handler : lessdate
    }, {
        title: '最少步骤',
        state:3,
        url: 'template/user/productList.html',
        handler : lessstep
    },{
        title: '最高成功率',
        state: 4,
        url: 'template/user/productList.html',
        handler : successrate
    }
    ];
$scope.datas = null;

$scope.tabs[3].handler();
$scope.currentTab = $scope.tabs[3];


$scope.onClickTab = function (tab) {
	tab.handler();
    $scope.currentTab = tab;
}
$scope.isActiveTab = function(state) {
    return state == $scope.currentTab.state;
}
$scope.showDetail = function(id){
	$location.path('/detail/'+id+'/'+operationId);
}

})
UserApp.controller('LoginCtrl', function($scope, $location, $routeParams){
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
				 userService.login($scope.loginid, $scope.password, $scope.picValidate);
				 $scope.$emit('login', 'wd');
				 $location.path('/first');
			 }catch(e){
				 alert(e.message);
			 }
		 }else {
	          alert('参数填写错误！');
	        }
	 }
});
UserApp.controller('UserCtrl', function($scope, $location, $routeParams){
	  $scope.user = {
			  name:"",
			  sex:"man",
			  picValidate:"",
			  tel:"",
			  telValidate:""
	  }
	 
	  $scope.complete = function(){
		  $scope.userForm.submitted = false;
		  
		  var operationType = $routeParams.type;
		  var operationAmount = $routeParams.amount;
		  var operationPeriod = $routeParams.period;
		  var province = $routeParams.province;
		  var city = $routeParams.city;
		  
	      if ($scope.userForm.$valid) {
	    	  var user = {"_t_":"fsm.model.User","name":$scope.user.name, "loginId":$scope.user.tel, "password" : "111111", "tel" : $scope.user.tel, "email" : ""};
	    	  var operationId = null;
	    	  try{
	    		  userService.registerInner(user);
	    		  operationId = operationService.applyOperation(parseInt(operationType), parseInt(operationAmount), parseInt(operationPeriod), province, city);
	    	  }catch(e){
	    		  alert(e.message);
	    		  return;
	    	  }
	    	  $location.path('/select/'+operationId);
	        } else {
	          $scope.userForm.submitted = true;
	          alert('参数填写错误！');
	        }
	  }
});