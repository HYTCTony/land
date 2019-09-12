"use strict";
//旧全国天地图(EPSG:4326)
ol.source.MapWorld_old = function(options) {
    if(!options) options = {};
    var type = {
        'vector_tile': 'http://t{0-7}.tianditu.com/DataServer?T=vec_c&x={x}&y={y}&l={z}&tk=522d0d6e3d54e2465c067e19a99b43f8',
        'vector_label': 'http://t{0-7}.tianditu.com/DataServer?T=cva_c&x={x}&y={y}&l={z}&tk=522d0d6e3d54e2465c067e19a99b43f8',
        'image_tile': 'http://t{0-7}.tianditu.cn/img_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=img&tileMatrixSet=c&TileMatrix={z}&TileRow={y}&TileCol={x}&style=default&format=tiles',
        'image_label': 'http://t{0-7}.tianditu.cn/cia_c/wmts?service=wmts&request=GetTile&version=1.0.0&LAYER=cia&tileMatrixSet=c&TileMatrix={z}&TileRow={y}&TileCol={x}&style=default&format=tiles',
    }
    ol.source.XYZ.call(this, {
        projection: ol.proj.get('EPSG:4326'),
        url: type[options.type],
        maxZoom: options.maxZoom,
        minZoom: options.minZoom,
        wrapX: options.warpX,
    });
};
ol.inherits(ol.source.MapWorld_old, ol.source.XYZ);

var olmap = {
    /**
     * @param {JSONObject} option
     * {
     * 	 view: [ol.View], default is getView()
     *   zoom: [false/true], default is true
     *   rotate: [false/true], default is false
     *   baseLayers: array of ol.layer.Layer, default is getBaseLayers()	 
     * 	 target: the target element, default is document.getElementById('map')
     * 	 callback: function(...), default is undefined
     * }
     */
    start: function(option) {
        var self = this;
        option = $.extend({
            view: self.getView(),
            zoom: true,
            rotate: false,
            baseLayers: self.getBaseLayers(),
            target: document.getElementById('map'),
            callback: undefined
        }, option);

        this.view = option.view;
        this.baseLayers = option.baseLayers;
        this.map = new ol.Map({
            controls: ol.control.defaults({
                zoom: option.zoom,
                rotate: option.rotate,
                attribution: false
            }),
            layers: this.baseLayers,
            target: option.target,
            view: this.view,
            logo: false
        });

        //定义辅助变量
        var earth_radius = 6378137; //地球半径，根据实际情况调整
        this.wgs84Sphere = new ol.Sphere(earth_radius);

        if(typeof option.callback === 'function') {
            option.callback(self);
        }
    },
    getView: function() {
        return new ol.View({
            center: [108.260, 22.845513],
            minZoom: 10,
            maxZoom: 21,
            zoom: 18,
            projection: ol.proj.get('EPSG:4326')
        });
    },
    /**
     * 获取直角比例尺
     * @param {element} target the target element, default is undefined
     */
    getRightAngleScaleLine: function(target) {
        //直角比例尺
        return new ol.control.ScaleLine({
            target: target,
            units: 'metric',
            className: 'ol-right-angle-scale-line',
            render: function(mapEvent) {
                ol.control.ScaleLine.render.apply(this, arguments);
                var $el = $('.ol-right-angle-scale-line-inner');
                $el.height($el.width());
            }
        });
    },
    /**
     * 获取基础图层
     */
    getBaseLayers: function() {
        var out = [];
        out.push(
            new ol.layer.Group({
                title: '城市地图',
                type: 'base',
                layers: [
                    new ol.layer.Tile({
                        source: new ol.source.MapWorld_old({type: 'vector_tile'})
                    }),
                    new ol.layer.Tile({
                        source: new ol.source.MapWorld_old({type: 'vector_label'})
                    }),
                ]
            })
        );
        return out;
    },
    /**
     * 平移地图到指定位置
     * @param {JSONObject} option
     * {
     * 	 position: ol.coordinate, not null
     *   duration: animation time length, default is 500 ms
     * }
     */
    panToPosition: function(option) {
        option = $.extend({
            duration: 500
        }, option);

        this.view.animate({
            center: option.position,
            duration: option.duration
        });
    },
    /**
     * 旋转地图
     * @param {JSONObject} option
     */
    rotate: function(option) {
        option = $.extend({
            duration: 500
        }, option);

        this.view.animate({
            rotation: option.rotation,
            duration: option.duration
        });
    },
    /**
     * 计算多边形面积
     * @param {ol.geom.Polygon/ol.geom.MultiPolygon} polygon
     * @return {number} 单位：亩
     */
    getArea: function(polygon) {
        var self = this;
        //单个多边形
        if(polygon instanceof ol.geom.Polygon) {
            //计算多边形外环面积
            var coor_arr = polygon.getLinearRing(0).getCoordinates();
            var area = Math.abs(this.wgs84Sphere.geodesicArea(coor_arr));

            //逐个减去内环面积
            if(polygon.getLinearRingCount() > 1) {
                for(var i = 1; i < polygon.getLinearRingCount() - 1; i++) {
                    var inner_coor_arr = polygon.getLinearRing(i).getCoordinates();
                    var inner_area = Math.abs(this.wgs84Sphere.geodesicArea(inner_coor_arr));
                    area = area - inner_area;
                }
            }

            //转换为单位：亩
            area = area * 0.0015;
            return area;

        }
        //多个多边形
        else if(polygon instanceof ol.geom.MultiPolygon) {
            var area = 0;
            //拆解为多个多边形分别叠加面积
            $.each(polygon.getPolygons(), function(i, polygon) {
                area = area + self.getArea(polygon);
            });
            return area;
        }
        return 0;
    },
    /**
     * 计算轨迹长度
     * @param {ol.geom.LineString} linestring
     * @return {Number} 单位：米
     */
    getLength: function(linestring) {
        if(linestring instanceof ol.geom.LineString) {
            var length = 0;
            //分别叠加每一段线段的长度
            var coor_arr = LineString.getCoordinates();
            for(var i = 0; i < coor_arr.length - 1; i++) {
                var per_length = this.wgs84Sphere.haversineDistance(coor_arr[i], coor_arr[i + 1]);
                length = length + per_length;
            }
            return length;
        }
        return null;
    }

}