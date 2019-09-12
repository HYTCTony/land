"use strict";
var main = {
    /**
     * 变量&数据
     */
    _data: {
        //地图锁定状态
        state_maplock: false,
        //罗盘开启状态
        state_compass: false,
        //地块编辑状态
        state_land_edit: false,
        //地块测量状态
        state_land_measure: false,
        //地块测量进行时状态
        state_land_measure_during: false,
        //普通状态下的MapView
        normal_map_view: undefined
    },
    /**
     * 初始化方法
     */
    start: function(options) {
        var self = this;
        this.olmap = options.olmap;
        
        //添加图层到地图
        $.each(this.vectorLayers, function(key, layer) {
            self.olmap.map.addLayer(layer);
        });
        
        //定义地图按钮控件
        $('.map-action > button.action-compass').on('click', function() {
            self.switchCompass(!$(this).hasClass('active'));
        });
        $('.map-action > button.action-maplock').on('click', function() {
            self.switchMaplock(!$(this).hasClass('active'));
        });
        $('.map-action > button.action-measure').on('click', function() {
            if(!$(this).hasClass('active')) {
                self.switchLandMeasure('open');
            }
        });
        $('.map-action > button.action-locate').on('click', function() {
            var $this = $(this);
            $this.addClass('active');
            setTimeout(function() {$this.removeClass('active');}, 100);
            
            if(self.getLastLocation()) {
                self.locateToPoint(self.getLastLocation());
            }
        });
        
        //罗盘图标旋转
        this.olmap.view.on('change:rotation', function(event) {
            var radian = event.target.get(event.key);
            $('.action-compass').css({
                transform: 'rotate(' + radian + 'rad)',
                transition: 'all 0s ease-in-out'
            });
        });
        
        //单击选中[附近地块]或[列表地块]
        this.olmap.map.on('click', function(event) {
            //检查是否选中了地块
            var flag = self.olmap.map.hasFeatureAtPixel(event.pixel, {
                layerFilter: function(layer) {
                    //过滤图层，只对[周边地块]和[列表地块]图层生效
                    return self.vectorLayers.landAround == layer || self.vectorLayers.landList == layer;
                }
            })

            if(flag && !self._data.state_land_edit && !self._data.state_land_measure) {
                //选中的地块高亮
                self.olmap.map.forEachFeatureAtPixel(event.pixel, function(feature, layer) {
                    self.switchLandBright(true, feature);
                    //通知安卓已选中地块，需要打开地块信息表单
                    android.getForm(feature.getId());
                    
                }, {
                    layerFilter: function(layer) {
                        //过滤图层，只对[周边地块]和[列表地块]图层生效
                        return self.vectorLayers.landAround == layer || self.vectorLayers.landList == layer;
                    }
                });
            } else {
                //取消地块高亮
                self.switchLandBright(false);
            }
        });
        
        //地图移动时加载附近地块
        this.olmap.map.on('moveend', function(event) {
            var view = self.olmap.map.getView();
            var resolution = view.getResolution();
            if(resolution <= 0.000021457672119140625) {//级别大于等于16级
                var extent = view.calculateExtent(self.olmap.map.getSize());
                var cacheExtent = self.olmap.moveCacheExtent;
                if(!cacheExtent
                    || !(
                        cacheExtent[0] < extent[0] && extent[2] < cacheExtent[2]
                        && cacheExtent[1] < extent[1] && extent[3] < cacheExtent[3]
                    )) {
                    
                    var a = extent[2] - extent[0];
                    var b = extent[3] - extent[1];
                    //缓冲区域
                    cacheExtent = [
                        extent[0] - a,
                        extent[1] - b,
                        extent[2] + a,
                        extent[3] + b
                    ];
                    
                    //更新缓冲区域
                    self.olmap.moveCacheExtent = cacheExtent;
                    //通知安卓，获取附近地块的数据
                    android.loadNearbyLand(cacheExtent[0], cacheExtent[1], cacheExtent[2], cacheExtent[3]);
                }
            }
        });
        
    },
    /**
     * 创建地块Feature
     * @param {JSONObject} data
     * {
     *   id: 地块ID,
     *   geo_json: 地块GeoJSON
     *   area: 地块面积，单位：亩
     *   attribute: 地块属性
     * }
     */
    createLandFeature: function(data) {
        var item = data;
        if(item && item.id != null && item.geo_json) {
            var feature = new ol.Feature({
                geometry: new ol.format.GeoJSON().readGeometry(item.geo_json),
                attributes: item.attributes
            })
            feature.setId(item.id);
            feature.set('item_id', item.id);
            feature.set('item_area', item.area >= 0 ? item.area.toFixed(3) + ' 亩' : '');
            feature.set('item_area_value', item.area >=0 ? Number(item.area.toFixed(3)) : 0);
            return feature;
        } else {
            return null;
        }
    },
    /**
     * 加载周边地块
     * @param {JSONArray} data
     * [{
     *   id: 地块ID,
     *   geo_json: 地块GeoJSON,
     *   area: 地块面积，单位：亩
     *   attribute: 地块属性
     * }, {...}]
     */
    loadLandAround: function(data) {
        var self = this;
        var featureArr = [];
        var landAroundLayerSource = this.vectorLayers.landAround.getSource();
        var landListLayerSource = this.vectorLayers.landList.getSource();
        var landMeasureLayerSource = this.vectorLayers.landMeasure.getSource();
        //筛选地块后添加到[周边地块]图层
        $.each(data, function(i, item) {
            //[列表地块]和[地块测量]图层中出现的地块要从[周边地块]图层去除
            if(landListLayerSource.getFeatureById(item.id)
                || landMeasureLayerSource.getFeatureById(item.id)) {
                return true;
            }
            var feature = self.createLandFeature(item);
            feature.set('type', 'LandAround');
            featureArr.push(feature);
        });
        landAroundLayerSource.addFeatures(featureArr);
    },
    /**
     * 清除周边地块
     */
    clearLandAround: function() {
        this.vectorLayers.landAround.getSource().clear();
    },
    /**
     * 加载列表地块
     * @param {JSON} data
     * [{
     *   id: 地块ID,
     *   geo_json: 地块GeoJSON
     *   area: 地块面积，单位：亩
     *   attribute: 地块属性
     * }, {...}]
     */
    loadLandList: function(data) {
        var self = this;
        var featureArr = [];
        var landAroundLayerSource = this.vectorLayers.landAround.getSource();
        $.each(data, function(i, item) {
            //从[周边地块]图层中移除重复的地块
            var repeatedFeature  = landAroundLayerSource.getFeatureById(item.id);
            if(repeatedFeature) {
                landAroundLayerSource.removeFeature(repeatedFeature);
            }
            
            var feature = self.createLandFeature(item);
            feature.set('type', 'LandList');
            featureArr.push(feature);
        });
        this.vectorLayers.landList.getSource().addFeatures(featureArr);
    },
    /**
     * 清除列表地块
     */
    clearLandList: function() {
        this.vectorLayers.landList.getSource().clear();
    },
    /**
     * 罗盘开关
     * @param {Boolean} active
     */
    switchCompass: function(active) {
        if(active) {
            //此处需要检查是否已获得定位信息，如果没有获得，则不允许打开罗盘
            if(this.getLastLocation() == null) return;
            //定位点回到正北
            var point = this.vectorLayers.location.getSource().getFeatureById('Location');
            if(point) {
                point.set('rotation', 0);
            }
            //标记状态
            this._data.state_compass = true;
            //强制开启地图锁定
            this.switchMaplock(true);
            $('.map-action > button.action-compass').addClass('active');
        } else {
            //地图旋转回到正北
            this.olmap.rotate({
                rotation: 0
            });
            //标记状态
            this._data.state_compass = false;
            $('.map-action > button.action-compass').removeClass('active');
        }
    },
    /**
     * 地图锁定开关
     * @param {Boolean} active
     */
    switchMaplock: function(active) {
        if(active) {
            //此处需要检查是否已获得定位信息，如果没有获得，则不允许打开地图锁定
            if(this.getLastLocation() == null) return;
            else this.locateToPoint(this.getLastLocation());
            
            
            olmap.map.getInteractions().forEach(function(interaction, i) {
                if(interaction instanceof ol.interaction.DragPan) {
                    //禁用拖拽地图
                    interaction.setActive(false);
                }
            });
            $('.map-action > button.action-maplock').addClass('active');
            //标记状态
            this._data.state_maplock = true;
        } else {
            olmap.map.getInteractions().forEach(function(interaction, i) {
                if(interaction instanceof ol.interaction.DragPan) {
                    //启用拖拽地图
                    interaction.setActive(true);
                }
            });
            $('.map-action > button.action-maplock').removeClass('active');
            //标记状态
            this._data.state_maplock = false;
            //强制关闭罗盘
            this.switchCompass(false);
        }
    },
    /**
     * 高亮地块
     * @param {Boolean} active
     * @param {ol.Feature} feature 地块Feature
     */
    switchLandBright: function(active, feature) {
        var source = this.vectorLayers.landBright.getSource();
        source.clear();
        //非地块编辑、地块测量状态下允许高亮地块
        if(active && !this._data.state_land_edit && !this._data.state_land_measure) {
            if(feature) source.addFeature(feature.clone());
        }
    },
    /**
     * 地块编辑开关
     * @param {String} action 动作：open/close/cancel
     * @param {Number} landId 地块ID
     * eg: switchLandEdit('open');//新建地块
     * eg: switchLandEdit('close', landId);//新建地块保存后，传入新地块的landId
     * eg: switchLandEdit('open', landId);//对地块ID为landId的地块进行编辑
     */
    switchLandEdit: function(action, landId) {
        //打开地块编辑
        if(action === 'open') {
            //清空[地块编辑]图层
            this.vectorLayers.landEdit.getSource().clear();
            
            if(landId != null) {
                var feature = null;
                //从[列表地块]获取Feature
                feature = this.vectorLayers.landList.getSource().getFeatureById(landId);
                if(feature) {
                    this.vectorLayers.landList.getSource().removeFeature(feature);
                } else {
                    //从[周边地块]获取Feature
                    feature = this.vectorLayers.landAround.getSource().getFeatureById(landId);
                    if(feature) {
                        this.vectorLayers.landAround.getSource().removeFeature(feature);
                    }
                }
                
                if(feature) {
                    //Feature添加到[地块编辑]图层
                    this.vectorLayers.landEdit.getSource().addFeature(feature);
                }
            }
            
            //标记状态
            this._data.state_land_edit = true;
            //取消地块高亮
            this.switchLandBright(false);
        }
        //关闭地块编辑
        else if(action === 'close') {
            var features = null;
            if(this.vectorLayers.landEdit.getVisible()) {
                //如果[地块编辑]图层可见，说明没有进行地块测量，直接从此图层获取地块
                features = this.vectorLayers.landEdit.getSource().getFeatures();
            } else {
                features = this.vectorLayers.landEdit.getSource().getFeatures();
            }
            if(features && features.length > 0) {
                var feature = features[0];
                //Feature ID存在，说明是对已存在的地块进行了修改
                if(feature.getId() != null) {
                    //将修改后的地块添加回原来的图层
                    var type = feature.get('type');
                    if(type === 'LandList') {
                        this.vectorLayers.landList.getSource().addFeature(feature);
                    } else if(type === 'LandAround') {
                        this.vectorLayers.landAround.getSource().addFeature(feature);
                    }
                }
                //Feature ID不存在，是新建的地块
                else {
                    //根据传入的landId设置新地块的ID，然后添加到周边地块
                    feature.setId(landId);
                    feature.set('type', 'LandAround');
                    this.vectorLayers.landAround.getSource().addFeature(feature);
                }
                //显示[地块编辑]图层，并清空
                this.vectorLayers.landEdit.setVisible(true);
                this.vectorLayers.landEdit.getSource().clear();
                //清空[地块测量]图层
                this.vectorLayers.landMeasure.getSource().clear();
                
                //标记状态
                this._data.state_land_edit = false;
                //高亮地块
                this.switchLandBright(true, feature);
                
            } else {
                //如果测量地块不存在，强制变为取消编辑
                this.switchLandEdit('cancel');
                return;
            }
        }
        //取消地块编辑
        else if(action === 'cancel') {
            //获取原始Feature，并添加回原来的图层
            var features = this.vectorLayers.landEdit.getSource().getFeatures();
            if(features && features.length > 0) {
                var feature = features[0];
                var type = feature.get('type');
                if(type === 'LandList') {
                    this.vectorLayers.landList.getSource().addFeature(feature);
                } else if(type === 'LandAround') {
                    this.vectorLayers.landAround.getSource().addFeature(feature);
                }
            }
            //显示[地块编辑]图层，并清空
            this.vectorLayers.landEdit.setVisible(true);
            this.vectorLayers.landEdit.getSource().clear();
            //清空[地块测量]图层
            this.vectorLayers.landMeasure.getSource().clear();
            
            //标记状态
            this._data.state_land_edit = false;
        }
    },
    /**
     * 地块测量开关
     * @param {String} action 动作：open/close/cancel
     */
    switchLandMeasure: function(action) {
        //打开地块测量
        if(action === 'open' && this._data.state_land_edit) {
            //此处需要检查是否已获得定位信息，如果没有获得，则不允许打开地块测量
            if(this.getLastLocation() == null) return;
            
            //清空[地块测量]和[地块测量标注]图层
            this.vectorLayers.landMeasure.getSource().clear();
            this.vectorLayers.landMeasureMarker.getSource().clear();
            //隐藏[地块编辑]图层
            this.vectorLayers.landEdit.setVisible(false);
            //从[地块编辑]图层获取Feature克隆后添加到[地块测量]图层
            var features = this.vectorLayers.landEdit.getSource().getFeatures();
            if(features && features.length > 0) {
                var feature = features[0];
                this.vectorLayers.landMeasure.getSource().addFeature(feature.clone());
                //通知安卓开启测量面板
                android.startMeasure(true, feature.get('item_area_value'));
            } else {
                //通知安卓开启测量面板
                android.startMeasure(false, 0);
            }
            
            //锁定视野，如果当前缩放级别小于20级，则放大到20级
            this._data.normal_map_view = this.olmap.view;
            this.olmap.view = new ol.View({
                center: this.getLastLocation(),
                minZoom: 16,
                maxZoom: 22,
                zoom: this._data.normal_map_view.getZoom(),
                projection: ol.proj.get('EPSG:4326')
            });
            if(this._data.normal_map_view.getZoom() < 20) {
                this.olmap.view.animate({
                    zoom: 20,
                    duration: 500
                });
            }
            this.olmap.map.setView(this.olmap.view);
            
            //切换图标
            $('.map-action > button.action-measure').addClass('active');
            //标记状态
            this._data.state_land_measure = true;
        }
        //关闭地块测量
        else if(action === 'close') {
            //获取“测量结果地块”
            var features = this.vectorLayers.landMeasure.getSource().getFeatures();
            if(features && features.length > 0) {
                var feature = features[0];
                
                //获取[地块编辑]图层中的地块
                var editFeature = null;
                var editFeatures = this.vectorLayers.landEdit.getSource().getFeatures();
                if(editFeatures && editFeatures.length > 0) {
                    editFeature = editFeatures[0];
                }
                if(editFeature) {
                    //填写ID和type
                    feature.setId(editFeature.getId());
                    feature.set('type', editFeature.get('type'));
                }
                //清空[地块测量标注]图层
                this.vectorLayers.landMeasureMarker.getSource().clear();
                
                //切换图标
                $('.map-action > button.action-measure').removeClass('active');
                //标记状态
                this._data.state_land_measure = false;
                
            } else {
                //如果测量地块不存在，强制变为取消测量
                this.switchLandEdit('cancel');
                return;
            }
        }
        //取消地块测量
        else if(action === 'cancel') {
            //强制取消测量
            this.landMeasureCancel();
            
            //清空[地块测量标注]图层
            this.vectorLayers.landMeasureMarker.getSource().clear();
            
            //切换图标
            $('.map-action > button.action-measure').removeClass('active');
            //标记状态
            this._data.state_land_measure = false;
        }
        
        
        if(action === 'close' || action === 'cancel') {
            //解锁视野
            this._data.normal_map_view.setCenter(this.olmap.view.getCenter());
            var zoom = this._data.normal_map_view.getZoom();
            this._data.normal_map_view.setZoom(this.olmap.view.getZoom());
            this.olmap.view = this._data.normal_map_view;
            this.olmap.map.setView(this.olmap.view);
            if(zoom < this.olmap.view.getZoom()) {
                this.olmap.view.animate({
                    zoom: zoom,
                    duration: 500
                });
            }
            this._data.normal_map_view = undefined;
        }
    },
    /**
     * 测量开始
     * @param {Boolean} remeasure 是否重测
     * @return {Boolean} success
     */
    landMeasureStart: function(remeasure) {
        if(!this._data.state_land_edit) return false;
        
        var landFeatures = this.vectorLayers.landMeasure.getSource().getFeatures();
        var markerFeature = this.vectorLayers.landMeasureMarker.getSource().getFeatures();
        if((landFeatures && landFeatures.length > 0) || (markerFeature && markerFeature.length > 0)) {
            //存在测量结果或测量标注，强制重测
            remeasure = true;
        }
        
        //重测时清除[地块测量]和[地块测量标注]图层
        if(remeasure) {
            this.vectorLayers.landMeasure.getSource().clear();
            this.vectorLayers.landMeasureMarker.getSource().clear();
        }
        
        //添加预览多边形
        var polygonMarkerFeature = new ol.Feature(new ol.geom.Polygon([]));
        polygonMarkerFeature.setId('PolygonMarker');
        this.vectorLayers.landMeasureMarker.getSource().addFeature(polygonMarkerFeature);
        
        //添加测量轨迹
        var lineMarkerFeature = new ol.Feature(new ol.geom.LineString([]));
        lineMarkerFeature.setId('LineMarker');
        this.vectorLayers.landMeasureMarker.getSource().addFeature(lineMarkerFeature);
        
        //标记状态
        this._data.state_land_measure_during = true;
        
        //通知安卓测量开始
        if(remeasure) android.alert('重新开始测量');
        else android.alert('开始测量');
        
        return true;
    },
    /**
     * 添加取点
     * @param {ol.coordinate} coordinate, can be null
     * @return {JSONObject}
     * {
     *   success {Boolean}: 是否成功
     *   point_count {Number}: 当前取点数
     * }
     */
    landMeasureAppend: function(coordinate) {
        //状态判断
        if(!this._data.state_land_measure_during) {
            return {
                success: false,
                point_count: 0
            };
        }
        
        //如果没有指定要取的点，则以最后定位位置取点
        if(!coordinate) coordinate = this.getLastLocation();
        //如果取点为空
        if(!coordinate) return false;
        
        var lineMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('LineMarker').getGeometry();
        //检查取点位置是否和轨迹最后一个点相同
        var lastCoordinate = lineMarker.getLastCoordinate();
        if(lastCoordinate && lastCoordinate[0] === coordinate[0] && lastCoordinate[1] === coordinate[1]) {
            //通知安卓
            android.alert('取点重复');
            return {
                success: false,
                point_count: lineMarker.getCoordinates().length
            };
        }
        //在测量轨迹上添加新点
        lineMarker.appendCoordinate(coordinate);

        var coorArr = lineMarker.getCoordinates();
        if(coorArr.length >= 3) {
            //重置预览多边形所有点
            coorArr.push(coorArr[0]); //添加一个与起始点重合的终点
            var polygonMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('PolygonMarker').getGeometry();
            polygonMarker.setCoordinates([coorArr]);
        }
        android.alert('取点成功');
        return {
            success: true,
            point_count: coorArr.length
        };
    },
    /**
     * 撤销上一个取点
     * @return {JSONObject}
     * {
     *   success {Boolean}: 是否成功
     *   point_count {Number}: 当前取点数
     * }
     */
    landMeasureUndo: function() {
        //状态判断
        if(!this._data.state_land_measure_during) {
            return {
                success: false,
                point_count: 0
            };
        }
        
        //在测量轨迹上删除最后一个点
        var lineMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('LineMarker').getGeometry();
        var coorArr = lineMarker.getCoordinates();
        if(coorArr.length == 0) {
            //通知安卓
            android.alert('没有可以撤销的取点');
            return {
                success: false,
                point_count: lineMarker.getCoordinates().length
            };
        }
        coorArr.pop();
        lineMarker.setCoordinates(coorArr);

        var coorArr = lineMarker.getCoordinates();
        if(coorArr.length >= 3) {
            //重置预览多边形所有点
            coorArr.push(coorArr[0]); //添加一个与起始点重合的终点
            var polygonMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('PolygonMarker').getGeometry();
            polygonMarker.setCoordinates([coorArr]);
        } else {
            //删除预览多边形所有点
            var polygonMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('PolygonMarker').getGeometry();
            polygonMarker.setCoordinates([]);
        }
        //通知安卓
        android.alert('撤销成功');
        return {
            success: true,
            point_count: coorArr.length
        };
    },
    /**
     * 测量结束
     * @return {false/JSON} result 操作失败时返回false
     * {
     *   area: area,            #面积，单位：亩
     *   geo_json: geo_json     #地块GeoJSON
     * }
     */
    landMeasureEnd: function() {
        //计算已取点数
        var lineMarker = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('LineMarker').getGeometry();
        var coorArr = lineMarker.getCoordinates();
        
        if(coorArr.length >= 3) {
            var polygon = this.vectorLayers.landMeasureMarker.getSource().getFeatureById('PolygonMarker').getGeometry();
            var area = this.olmap.getArea(polygon);

            //将测量结果添加到[地块测量]图层中显示
            var feature = new ol.Feature(polygon);
            feature.set('item_area', area >= 0 ? area.toFixed(3) + ' 亩' : '');
            feature.set('item_area_value', area >=0 ? Number(area.toFixed(3)) : 0);
            this.vectorLayers.landMeasure.getSource().addFeature(feature);
            //清空[地块测量标注]图层
            this.vectorLayers.landMeasureMarker.getSource().clear();

            //标记状态
            this._data.state_land_measure_during = false;
            //通知安卓
            android.alert('测量完成');
            return {
                area: area,
                geo_json: new ol.format.GeoJSON().writeGeometry(feature.getGeometry())
            };

        } else {
            //通知安卓取点数不足以构成面
            android.alert('至少需要3个点才能闭合');
            return false;
        }
    },
    /**
     * 取消测量
     */
    landMeasureCancel: function() {
        //清空[地块测量]和[地块测量标注]图层
        this.vectorLayers.landMeasure.getSource().clear();
        this.vectorLayers.landMeasureMarker.getSource().clear();
        //从[地块编辑]图层获取Feature克隆后添加到[地块测量]图层
        var features = this.vectorLayers.landEdit.getSource().getFeatures();
        if(features && features.length > 0) {
            var feature = features[0];
            this.vectorLayers.landMeasure.getSource().addFeature(feature.clone());
        }
        //标记状态
        this._data.state_land_measure_during = false;
        
        //通知安卓
        android.alert('测量已取消');
    },
    /**
     * 获取最近定位位置
     * @return {ol.coordinate/Null}
     */
    getLastLocation: function() {
        var point = this.vectorLayers.location.getSource().getFeatureById('Location');
        if(point) {
            var coordinate = point.getGeometry().getFirstCoordinate();
            return coordinate;
        }
        //通知安卓获取位置失败
        android.alert('获取位置信息失败，请稍后再试');
        return null;
    },
    /**
     * 更新最近定位位置
     * @param {ol.coordinate} coordinate
     */
    updateLocation: function(coordinate) {
        var point = this.vectorLayers.location.getSource().getFeatureById('Location');
        if(!point) {
            point = new ol.Feature({
                geometry: new ol.geom.Point(coordinate)
            });
            point.setId('Location');
            this.vectorLayers.location.getSource().addFeature(point);
            //首次获得定位，自动定位到最新位置
            this.locateToPoint(coordinate);
            
        } else {
            point.setGeometry(new ol.geom.Point(coordinate));
            //如果已开启地图锁定，定位到最新位置
            if(this._data.state_maplock) {
                this.locateToPoint(coordinate);
            }
        }
    },
    /**
     * 定位到点
     * @param {ol.coordinate} coordinate
     */
    locateToPoint: function(coordinate) {
        this.olmap.panToPosition({
            position: coordinate,
            duration: 500
        });
    },
    /**
     * 定位到地块
     * @param {Number} landId
     */
    locateToLand: function(landId) {
        var feature = this.vectorLayers.landList.getSource().getFeatureById(landId);
        if(feature) {
            var zoom = this.olmap.view.getZoom();
            this.olmap.view.fit(feature.getGeometry(), this.olmap.map.getSize());
            this.olmap.view.setZoom(zoom);
            this.switchLandBright(true, feature);
        }
    },
    /**
     * 矢量图层
     */
    vectorLayers: {
        //定位图层
        location: new ol.layer.Vector({
            source: new ol.source.Vector(),
            style: function(feature) {
                var rotation = feature.get('rotation');
                var style = new ol.style.Style({
                      image: new ol.style.Icon({
                          rotation: rotation,
                          anchor: [0.5,0.5],
                          src: './image/position.png'
                      })
                });
                return style;
            }
        }),
        //列表地块图层
        landList: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: 'rgba(52,168,52,0.4)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#0099FF',
                        width: 2
                    }),
                    text: new ol.style.Text({
                        text: feature.get('item_area') ? feature.get('item_area') : '',
                        fill: new ol.style.Fill({
                            color: '#fff'
                        })
                    })
                });
                return style;
            }
        }),
        //视野周边地块图层
        landAround: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,255,255,0.4)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#0099FF',
                        width: 2
                    }),
                    text: new ol.style.Text({
                        text: feature.get('item_area') ? feature.get('item_area') : '',
                        fill: new ol.style.Fill({
                            color: '#FF4040'
                        })
                    })
                });
                return style;
            }
        }),
        //高亮地块图层
        landBright: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,64,64,0.6)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#0099FF',
                        width: 2
                    }),
                    text: new ol.style.Text({
                        text: feature.get('item_area') ? feature.get('item_area') : '',
                        fill: new ol.style.Fill({
                            color: '#fff'
                        })
                    })
                });
                return style;
            }
        }),
        //地块编辑图层
        landEdit: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: 'rgba(255,64,64,0.6)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#0099FF',
                        width: 2
                    }),
                    text: new ol.style.Text({
                        text: feature.get('item_area') ? feature.get('item_area') : '',
                        fill: new ol.style.Fill({
                            color: '#fff'
                        })
                    })
                });
                return style;
            }
        }),
        //地块测量图层
        landMeasure: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var style = new ol.style.Style({
                    fill: new ol.style.Fill({
                        color: 'rgba(30,144,255,0.5)'
                    }),
                    stroke: new ol.style.Stroke({
                        color: '#fff',
                        width: 2
                    }),
                    text: new ol.style.Text({
                        text: feature.get('item_area') ? feature.get('item_area') : '',
                        fill: new ol.style.Fill({
                            color: '#FFF'
                        })
                    })
                });
                return style;
            }
        }),
        //地块测量标注图层
        landMeasureMarker: new ol.layer.Vector({
            maxResolution: 0.000021457672119140625,
            source: new ol.source.Vector(),
            style: function(feature) {
                var geometry = feature.getGeometry();

                var styles = [];
                //预览多边形
                if(geometry instanceof ol.geom.Polygon) {
                    styles.push(new ol.style.Style({
                        fill: new ol.style.Fill({
                            color: 'rgba(200,200,200,0.4)'
                        })
                    }));
                }

                //测量轨迹线
                if(geometry instanceof ol.geom.LineString) {
                    styles.push(
                        new ol.style.Style({
                            stroke: new ol.style.Stroke({
                                color: '#0099FF',
                                width: 3
                            })
                        })
                    );

                    var coorArr = geometry.getCoordinates();
                    for(var i = 0; i < coorArr.length; i++) {
                        if(i == 0) {
                            styles.push(new ol.style.Style({
                                geometry: new ol.geom.Point(coorArr[i]),
                                image: new ol.style.Circle({
                                    radius: 8,
                                    fill: new ol.style.Fill({
                                        color: 'green'
                                    }),
                                    stroke: new ol.style.Stroke({
                                        color: '#FFF',
                                        width: 2
                                    })
                                }),
                                text: new ol.style.Text({
                                    text: 'S',
                                    fill: new ol.style.Fill({
                                        color: '#FFF'
                                    })
                                })
                            }));

                        } else if(i == coorArr.length - 1) {
                            styles.push(new ol.style.Style({
                                geometry: new ol.geom.Point(coorArr[i]),
                                image: new ol.style.Circle({
                                    radius: 8,
                                    fill: new ol.style.Fill({
                                        color: 'purple'
                                    }),
                                    stroke: new ol.style.Stroke({
                                        color: '#FFF',
                                        width: 2
                                    })
                                }),
                                text: new ol.style.Text({
                                    text: 'E',
                                    fill: new ol.style.Fill({
                                        color: '#FFF'
                                    })
                                })
                            }));

                        } else {
                            styles.push(new ol.style.Style({
                                geometry: new ol.geom.Point(coorArr[i]),
                                image: new ol.style.Circle({
                                    radius: 8,
                                    fill: new ol.style.Fill({
                                        color: 'rgba(255,48,48,0.6)'
                                    })
                                }),
                                text: new ol.style.Text({
                                    text: i == 0 ? 'S' : (i == coorArr.length - 1 ? 'E' : (i + 1).toString()),
                                    fill: new ol.style.Fill({
                                        color: '#FFF'
                                    })
                                })
                            }));
                        }
                    }
                }
                return styles;
            }
        })
    }
};

/**
 * 提供给安卓调用的方法
 */
var call = {
    /**
     * 旋转地图
     * @param {Number} radian 方向角（以正北为0，单位：弧度）
     */
    rotate: function(radian) {
        if(main._data.state_compass) {
            main.olmap.rotate({
                rotation: -radian,
                duration: 0
            });
        } else {
            var point = main.vectorLayers.location.getSource().getFeatureById('Location');
            if(point) {
                point.set('rotation', radian);
            }
        }
    },
    /**
     * 加载周边地块
     * @param {JSON} data
     * [{
     *   id: 地块ID,
     *   wkt: 地块wkt
     * }, {...}]
     */
    load_land_around: function(data) {
        var json = [];
        $.each(data, function(i, item) {
            var geometry = new ol.format.WKT().readGeometry(item.wkt)
            item.geo_polygon = new ol.format.GeoJSON().writeGeometry(geometry);
            item.geo_area = main.olmap.getArea(geometry);
            json.push({
                id: item.id,
                geo_json: item.geo_polygon,
                area: item.geo_area,
                attribute: item
            });
        });
        main.loadLandAround(json);
    },
    /**
     * 加载列表地块
     * @param {JSON} data
     * [{
     *   id: 地块ID,
     *   geo_polygon: 地块GeoJSON
     *   geo_area: 地块面积，单位：亩
     * }, {...}]
     * @param {Boolean} loadMore 加载更多 
     */
    load_land_list: function(data, loadMore) {
        var json = [];
        $.each(data, function(i, item) {
            json.push({
                id: item.id,
                geo_json: item.geo_polygon,
                area: Number(item.geo_area),
                attribute: item
            });
        });
        if(!loadMore) main.clearLandList();
        main.loadLandList(json);
        //重新加载周边地块
        if(!loadMore) {
            //通知安卓，获取附近地块的数据
            var cacheExtent = main.olmap.moveCacheExtent;
            if(cacheExtent) android.loadNearbyLand(cacheExtent[0], cacheExtent[1], cacheExtent[2], cacheExtent[3]);
        }
    }
}
