/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function applyMargins() {
    var leftToggler = $(".mini-submenu-left");
    if (leftToggler.is(":visible")) {
      $("#map .ol-zoom")
        .css("margin-left", 0)
        .removeClass("zoom-top-opened-sidebar")
        .addClass("zoom-top-collapsed");
    } else {
      $("#map .ol-zoom")
        .css("margin-left", $(".sidebar-body").width())
        .removeClass("zoom-top-opened-sidebar")
        .removeClass("zoom-top-collapsed");
        console.log($(".sidebar-body").width());
    }
}

function isConstrained() {
  return $(".sidebar").width() === $(window).width();
}

function applyInitialUIState() {
  if (isConstrained()) {
    $(".sidebar-left .sidebar-body").fadeOut('slide');
    $('.mini-submenu-left').fadeIn();
  }
}

$(function(){
    applyInitialUIState();
    //applyMargins();
    
    $('.sidebar-left .slide-submenu').on('click',function() {
      var thisEl = $(this);
      thisEl.closest('.sidebar-body').fadeOut('slide',function(){
        $('.mini-submenu-left').fadeIn();
        //applyMargins();
      });
    });

    $('.mini-submenu-left').on('click',function() {
      var thisEl = $(this);
      $('.sidebar-left .sidebar-body').toggle('slide');
      thisEl.hide();
      //applyMargins();
    });

    //$(window).on("resize", applyMargins);
});