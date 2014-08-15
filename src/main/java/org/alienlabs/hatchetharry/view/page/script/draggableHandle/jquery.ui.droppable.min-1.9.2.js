/*! jQuery UI - v1.9.2 - 2013-06-12
 * http://jqueryui.com
 * Includes: jquery.ui.droppable.js
 * Copyright 2013 jQuery Foundation and other contributors; Licensed MIT */
(function (e, t) {
    e.widget("ui.droppable", {version: "1.9.2", widgetEventPrefix: "drop", options: {accept: "*", activeClass: !1, addClasses: !0, greedy: !1, hoverClass: !1, scope: "default", tolerance: "intersect"}, _create: function () {
        var t = this.options, n = t.accept;
        this.isover = 0, this.isout = 1, this.accept = e.isFunction(n) ? n : function (e) {
            return e.is(n)
        }, this.proportions = {width: this.element[0].offsetWidth, height: this.element[0].offsetHeight}, e.ui.ddmanager.droppables[t.scope] = e.ui.ddmanager.droppables[t.scope] || [], e.ui.ddmanager.droppables[t.scope].push(this), t.addClasses && this.element.addClass("ui-droppable")
    }, _destroy: function () {
        var t = e.ui.ddmanager.droppables[this.options.scope];
        for (var n = 0; n < t.length; n++)t[n] == this && t.splice(n, 1);
        this.element.removeClass("ui-droppable ui-droppable-disabled")
    }, _setOption: function (t, n) {
        t == "accept" && (this.accept = e.isFunction(n) ? n : function (e) {
            return e.is(n)
        }), e.Widget.prototype._setOption.apply(this, arguments)
    }, _activate: function (t) {
        var n = e.ui.ddmanager.current;
        this.options.activeClass && this.element.addClass(this.options.activeClass), n && this._trigger("activate", t, this.ui(n))
    }, _deactivate: function (t) {
        var n = e.ui.ddmanager.current;
        this.options.activeClass && this.element.removeClass(this.options.activeClass), n && this._trigger("deactivate", t, this.ui(n))
    }, _over: function (t) {
        var n = e.ui.ddmanager.current;
        if (!n || (n.currentItem || n.element)[0] == this.element[0])return;
        this.accept.call(this.element[0], n.currentItem || n.element) && (this.options.hoverClass && this.element.addClass(this.options.hoverClass), this._trigger("over", t, this.ui(n)))
    }, _out: function (t) {
        var n = e.ui.ddmanager.current;
        if (!n || (n.currentItem || n.element)[0] == this.element[0])return;
        this.accept.call(this.element[0], n.currentItem || n.element) && (this.options.hoverClass && this.element.removeClass(this.options.hoverClass), this._trigger("out", t, this.ui(n)))
    }, _drop: function (t, n) {
        var r = n || e.ui.ddmanager.current;
        if (!r || (r.currentItem || r.element)[0] == this.element[0])return!1;
        var i = !1;
        return this.element.find(":data(droppable)").not(".ui-draggable-dragging").each(function () {
            var t = e.data(this, "droppable");
            if (t.options.greedy && !t.options.disabled && t.options.scope == r.options.scope && t.accept.call(t.element[0], r.currentItem || r.element) && e.ui.intersect(r, e.extend(t, {offset: t.element.offset()}), t.options.tolerance))return i = !0, !1
        }), i ? !1 : this.accept.call(this.element[0], r.currentItem || r.element) ? (this.options.activeClass && this.element.removeClass(this.options.activeClass), this.options.hoverClass && this.element.removeClass(this.options.hoverClass), this._trigger("drop", t, this.ui(r)), this.element) : !1
    }, ui: function (e) {
        return{draggable: e.currentItem || e.element, helper: e.helper, position: e.position, offset: e.positionAbs}
    }}), e.ui.intersect = function (t, n, r) {
        if (!n.offset)return!1;
        var i = (t.positionAbs || t.position.absolute).left, s = i + t.helperProportions.width, o = (t.positionAbs || t.position.absolute).top, u = o + t.helperProportions.height, a = n.offset.left, f = a + n.proportions.width, l = n.offset.top, c = l + n.proportions.height;
        switch (r) {
            case"fit":
                return a <= i && s <= f && l <= o && u <= c;
            case"intersect":
                return a < i + t.helperProportions.width / 2 && s - t.helperProportions.width / 2 < f && l < o + t.helperProportions.height / 2 && u - t.helperProportions.height / 2 < c;
            case"pointer":
                var h = (t.positionAbs || t.position.absolute).left + (t.clickOffset || t.offset.click).left, p = (t.positionAbs || t.position.absolute).top + (t.clickOffset || t.offset.click).top, d = e.ui.isOver(p, h, l, a, n.proportions.height, n.proportions.width);
                return d;
            case"touch":
                return(o >= l && o <= c || u >= l && u <= c || o < l && u > c) && (i >= a && i <= f || s >= a && s <= f || i < a && s > f);
            default:
                return!1
        }
    }, e.ui.ddmanager = {current: null, droppables: {"default": []}, prepareOffsets: function (t, n) {
        var r = e.ui.ddmanager.droppables[t.options.scope] || [], i = n ? n.type : null, s = (t.currentItem || t.element).find(":data(droppable)").andSelf();
        e:for (var o = 0; o < r.length; o++) {
            if (r[o].options.disabled || t && !r[o].accept.call(r[o].element[0], t.currentItem || t.element))continue;
            for (var u = 0; u < s.length; u++)if (s[u] == r[o].element[0]) {
                r[o].proportions.height = 0;
                continue e
            }
            r[o].visible = r[o].element.css("display") != "none";
            if (!r[o].visible)continue;
            i == "mousedown" && r[o]._activate.call(r[o], n), r[o].offset = r[o].element.offset(), r[o].proportions = {width: r[o].element[0].offsetWidth, height: r[o].element[0].offsetHeight}
        }
    }, drop: function (t, n) {
        var r = !1;
        return e.each(e.ui.ddmanager.droppables[t.options.scope] || [], function () {
            if (!this.options)return;
            !this.options.disabled && this.visible && e.ui.intersect(t, this, this.options.tolerance) && (r = this._drop.call(this, n) || r), !this.options.disabled && this.visible && this.accept.call(this.element[0], t.currentItem || t.element) && (this.isout = 1, this.isover = 0, this._deactivate.call(this, n))
        }), r
    }, dragStart: function (t, n) {
        t.element.parentsUntil("body").bind("scroll.droppable", function () {
            t.options.refreshPositions || e.ui.ddmanager.prepareOffsets(t, n)
        })
    }, drag: function (t, n) {
        t.options.refreshPositions && e.ui.ddmanager.prepareOffsets(t, n), e.each(e.ui.ddmanager.droppables[t.options.scope] || [], function () {
            if (this.options.disabled || this.greedyChild || !this.visible)return;
            var r = e.ui.intersect(t, this, this.options.tolerance), i = !r && this.isover == 1 ? "isout" : r && this.isover == 0 ? "isover" : null;
            if (!i)return;
            var s;
            if (this.options.greedy) {
                var o = this.options.scope, u = this.element.parents(":data(droppable)").filter(function () {
                    return e.data(this, "droppable").options.scope === o
                });
                u.length && (s = e.data(u[0], "droppable"), s.greedyChild = i == "isover" ? 1 : 0)
            }
            s && i == "isover" && (s.isover = 0, s.isout = 1, s._out.call(s, n)), this[i] = 1, this[i == "isout" ? "isover" : "isout"] = 0, this[i == "isover" ? "_over" : "_out"].call(this, n), s && i == "isout" && (s.isout = 0, s.isover = 1, s._over.call(s, n))
        })
    }, dragStop: function (t, n) {
        t.element.parentsUntil("body").unbind("scroll.droppable"), t.options.refreshPositions || e.ui.ddmanager.prepareOffsets(t, n)
    }}
})(jQuery);