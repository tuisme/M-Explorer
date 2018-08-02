$(document).ready(function() {
    $('#datatables').DataTable( {
        serverSide: true,
        ajax: function ( data, callback, settings ) {
            var out = [];

            for ( var i=data.start, ien=data.start+data.length ; i<ien ; i++ ) {
                out.push( [ i+'-1', i+'-2', i+'-3', i+'-4', i+'-5',i+'-6', i+'-7', i+'-8', i+'-9', i+'-10' ] );
            }

            setTimeout( function () {
                callback( {
                    draw: data.draw,
                    data: out,
                    recordsTotal: 600,
                    recordsFiltered: 600
                } );
            }, 50 );
        },
        scrollY: 400,
        scroller: {
            loadingIndicator: true
        },
        stateSave: true
    } );
} );
