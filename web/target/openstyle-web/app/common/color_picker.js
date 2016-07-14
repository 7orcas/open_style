/**
 *
 * HTML5 Color Picker
 *
 * Changed to work with angularjs.
 *
 * Licensed under the MIT license.
 * http://www.opensource.org/licenses/mit-license.php
 * 
 * Copyright 2012, Script Tutorials
 * http://www.script-tutorials.com/
 * 
 * Thanks to http://css.dzone.com/articles/creating-your-own-html5
 * 
 * @param record to update
 * @param name of setter function
 * @param controller scope
 */

function setColorPicker(rec, setFn, scope){
    
    // create canvas and context objects
    var canvas = document.getElementById('cpicker' + rec.getId());
    var ctx = canvas.getContext('2d');

    // drawing active image
    var image = new Image();
    image.src = 'img/colorwheel1.png';

    image.onload = function () {
        ctx.drawImage(image, 0, 0, image.width, image.height); // draw the image on the canvas
    };
    
    
    $('#cpicker' + rec.getId()).mousemove(function(e) { // mouse move handler
       // if (bCanPreview) {
            // get coordinates of current position
            var canvasOffset = $(canvas).offset();
            var canvasX = Math.floor(e.pageX - canvasOffset.left);
            var canvasY = Math.floor(e.pageY - canvasOffset.top);

            // get current pixel
            var imageData = ctx.getImageData(canvasX, canvasY, 1, 1);
            var pixel = imageData.data;

            // update rgb color
            if (rec.colorActive){
            	rec[setFn]('' + pixel[0] + ',' + pixel[1] + ',' + pixel[2]);
            }
            scope.$digest();

            // update controls
            $('#rVal' + rec.getId()).val(pixel[0]);
            $('#gVal' + rec.getId()).val(pixel[1]);
            $('#bVal' + rec.getId()).val(pixel[2]);
            $('#rgbVal' + rec.getId()).val(pixel[0]+','+pixel[1]+','+pixel[2]);

            var dColor = pixel[2] + 256 * pixel[1] + 65536 * pixel[0];
            $('#hexVal' + rec.getId()).val('#' + ('0000' + dColor.toString(16)).substr(-6));
      //  }
    });
    

    $('#colorpicker' + rec.getId()).fadeToggle("slow", "linear");

    $('#cpicker' + rec.getId()).click(function(e) { // click event handler
    	rec.colorActive = !rec.colorActive;
        $('#colorpicker' + rec.getId()).fadeToggle("slow", "linear");
    }); 

}

