 'use strict';

angular.module('app.login.controller', ['ngResource'])

    /**
     * Login form controller.<p>
     * 
     * This is a stand-alone controller. Once the user has successfully logged in, the browser is
     * directed to the main page. The value for the call to $window.location.href for the main page 
     * is returned from the server (after the login event).<p>
     * 
     * [License]
     * @author John Stewart
     */
    .controller('aLoginCtrl', [
		'$scope',
		'$rootScope',
		'$location',
		'$window',
		'$timeout',
		'alLoginObj',
		'aLang',
		'acGlobal',
		'focus',
		
		function($scope, $rootScope, $location, $window, $timeout, alLoginObj, aLang, acGlobal, focus) {

			/*
             * Attach basic language functions 
             */
            aLang.addFormFunctions($scope, 'login'); 
            
            var changeLangRequest = false;
            var changeLanguageX = $scope.changeLanguage; 
            $scope.changeLanguage = function(value){
            	changeLangRequest = true;
            	changeLanguageX(value);
            };
            
            
            $scope.luser = null;
            $scope.lpass = null;
            $scope.lcomp = null;
            $scope.llang = null;
            
            
            /*
             * Load user name, password and/or language from url
             */
            var x = $location.absUrl();
            var index = x.indexOf('?');
            var params = x.substring(index + 1).split('&');
            for (var i=0; i<params.length; i++){
                var param = params[i].split('=');
                var k = param[0];
                var v = param[1];

                if(k === 'u'){
                    $scope.luser=v;
                }
                if(k === 'p'){
                    $scope.lpass=v;
                }
                if(k === 'l'){
                    $scope.llang=v;
                }
                if(k === 'c'){
                    $scope.lcomp=v;
                }
            }
            
            
			/*
             * Load language from hidden field
             */
            if ($scope.llang === null){
                try{
                    $scope.llang = document.getElementById('langcode').value;
                }
                catch (err){}
            }

            if ($scope.llang !== null){
                $scope.changeLanguage($scope.llang);
            }

			
            
            $timeout(function (){
            	focus('focusMe');
    	    }, 150);

            //ToDo get site from server?            
            $scope.logo = 'img/head_logo_' + acGlobal.globals().site + '.jpg';
            
			/*
			 * Load language definitions (will be for set='login')
			 */
            var lang = $location.search().lang;
            if (!angular.isDefined(lang) || lang === null){
            	lang = acGlobal.globals().language;
            }
            
            $scope.selectedLang = null;
            if (angular.isDefined(lang) && lang !== null){
            	$scope.selectedLang = lang;	
            }
            
            aLang.loadLangLogin(lang, function(){
            	angular.element(".page_header").show();
            	angular.element(".page_login").show();
            });
			
			
			/*
			 * Recaptcha is used to prevent automated login attempts
			 */
			var recaptchaActive = false;
			$scope.showRecaptcha = function showRecaptchaFn(publicKey, element) {
	            Recaptcha.create(publicKey, element, {
	                theme: 'custom',
	                custom_theme_widget: 'recaptcha_widget'
	            });
	            angular.element("#recaptcha_widget").show();
	            recaptchaActive = true;
	        };
	        
	        /*
	        //Show Recaptcha by default
	        alLoginObj.captchaPublicKey(function(){
	        	$scope.showRecaptcha(alLoginObj.getCaptchaPublicKey(), 'recaptcha_div');
	        });
            */
			
	        
	        var setRequired = function(id){
	        	var el = document.getElementById(id);
	        	el.setAttribute('required', '');
	        };
	        
	        /*
	         * Form login action
	         */
	        $scope.login = function() {
	        	
	        	var test = true;
	        	if ($scope.luser === null || $scope.luser.length === 0){
	        		test = false;
	        		setRequired('inputUser');
	        	}
	        	if ($scope.lpass === null || $scope.lpass.length === 0){
	        		test = false;
	        		setRequired('inputPass');
	        	}
	        	if (!test){
	        		return;
	        	}
	        	
	        	try{
        			$scope.lcomp = parseInt($scope.lcomp);
        		}
        		catch (err){
        			$scope.lcomp = acGlobal.globals().company;
        		}
	        	
	        	var c = null;
				var r = null;
				if (recaptchaActive === true){
					c = Recaptcha.get_challenge();
					r = Recaptcha.get_response();
					
					if (r === null || r.length === 0){
						return;
					}
				}
				
				acGlobal.addLoading();
				alLoginObj.loginUser(
						$scope.lcomp, 
						$scope.luser, 
						$scope.lpass,
						$scope.selectedLang,
						changeLangRequest,
						c,
						r,
						acGlobal.removeLoading());
			};

		
			/*
			 * Successful login action 
			 */
			$scope.$on('event:loginSuccess', function() {
				$window.location.href = alLoginObj.getLocationHref();
			});

			
			/*
			 * Unsuccessful login action
			 */
			$scope.$on('event:loginFailed', function() {
				$scope.loginFailedMessage = alLoginObj.getMessage();
				$scope.lpass = null;
				
				if (alLoginObj.isShowCaptcha() === true){
					if (recaptchaActive === true){
						Recaptcha.reload();
					}
					else{
						$scope.showRecaptcha(alLoginObj.getCaptchaPublicKey(), 'recaptcha_div');
					}
				}
			});

			
			//Test if browser is at least to minimum standard
			var min = getUpdateBrowserMessage();
			$scope.update  = min !== null;

			if ($scope.update){
				console.log('Update=' + $scope.update + ', minimum=' + min);
			}
			
			$scope.getUpdateBrowserMessage = function(){
				if (!$scope.update){
					return null;
				}
				return aLang.label('UpdateBrowser', min);
			};
			
			
			if ($scope.luser !== null && $scope.lpass !== null){
				$scope.login();
			}
		}
        
	]);
