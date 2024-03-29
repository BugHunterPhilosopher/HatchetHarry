jQuery.atmosphere = function () {
    jQuery(window).bind("unload.atmosphere", function () {
        jQuery.atmosphere.unsubscribe()
    });
    jQuery(window).bind("offline", function () {
        jQuery.atmosphere.unsubscribe()
    });
    jQuery(window).keypress(function (b) {
        if (b.keyCode === 27) {
            b.preventDefault()
        }
    });
    var a = function (c) {
        var b, e = /^(.*?):[ \t]*([^\r\n]*)\r?$/mg, d = {};
        while (b = e.exec(c)) {
            d[b[1]] = b[2]
        }
        return d
    };
    return{version: "2.0.8-jquery", uuid: 0, requests: [], callbacks: [], onError: function (b) {
    }, onClose: function (b) {
    }, onOpen: function (b) {
    }, onMessage: function (b) {
    }, onReconnect: function (c, b) {
    }, onMessagePublished: function (b) {
    }, onTransportFailure: function (c, b) {
    }, onLocalMessage: function (b) {
    }, onClientTimeout: function (b) {
    }, onFailureToReconnect: function (c, b) {
    }, AtmosphereRequest: function (G) {
        var I = {timeout: 300000, method: "GET", headers: {}, contentType: "", callback: null, url: "", data: "", suspend: true, maxRequest: -1, reconnect: true, maxStreamingLength: 10000000, lastIndex: 0, logLevel: "info", requestCount: 0, fallbackMethod: "GET", fallbackTransport: "streaming", transport: "long-polling", webSocketImpl: null, webSocketBinaryType: null, dispatchUrl: null, webSocketPathDelimiter: "@@", enableXDR: false, rewriteURL: false, attachHeadersAsQueryString: true, executeCallbackBeforeReconnect: false, readyState: 0, lastTimestamp: 0, withCredentials: false, trackMessageLength: false, messageDelimiter: "|", connectTimeout: -1, reconnectInterval: 0, dropAtmosphereHeaders: true, uuid: 0, shared: false, readResponsesHeaders: false, maxReconnectOnClose: 5, enableProtocol: true, onError: function (av) {
        }, onClose: function (av) {
        }, onOpen: function (av) {
        }, onMessage: function (av) {
        }, onReopen: function (aw, av) {
        }, onReconnect: function (aw, av) {
        }, onMessagePublished: function (av) {
        }, onTransportFailure: function (aw, av) {
        }, onLocalMessage: function (av) {
        }, onFailureToReconnect: function (aw, av) {
        }, onClientTimeout: function (av) {
        }};
        var Q = {status: 200, reasonPhrase: "OK", responseBody: "", messages: [], headers: [], state: "messageReceived", transport: "polling", error: null, request: null, partialMessage: "", errorHandled: false, closedByClientTimeout: false};
        var T = null;
        var i = null;
        var p = null;
        var y = null;
        var A = null;
        var ae = true;
        var f = 0;
        var aq = false;
        var U = null;
        var al;
        var k = null;
        var D = jQuery.now();
        var E;
        var au;
        at(G);
        function am() {
            ae = true;
            aq = false;
            f = 0;
            T = null;
            i = null;
            p = null;
            y = null
        }

        function u() {
            ag();
            am()
        }

        function at(av) {
            u();
            I = jQuery.extend(I, av);
            I.mrequest = I.reconnect;
            if (!I.reconnect) {
                I.reconnect = true
            }
        }

        function j() {
            return I.webSocketImpl != null || window.WebSocket || window.MozWebSocket
        }

        function M() {
            return window.EventSource
        }

        function n() {
            if (I.shared) {
                k = ac(I);
                if (k != null) {
                    if (I.logLevel === "debug") {
                        jQuery.atmosphere.debug("Storage service available. All communication will be local")
                    }
                    if (k.open(I)) {
                        return
                    }
                }
                if (I.logLevel === "debug") {
                    jQuery.atmosphere.debug("No Storage service available.")
                }
                k = null
            }
            I.firstMessage = jQuery.atmosphere.uuid == 0 ? true : false;
            I.isOpen = false;
            I.ctime = jQuery.now();
            I.uuid = jQuery.atmosphere.uuid;
            I.closedByClientTimeout = false;
            if (I.transport !== "websocket" && I.transport !== "sse") {
                m(I)
            } else {
                if (I.transport === "websocket") {
                    if (!j()) {
                        K("Websocket is not supported, using request.fallbackTransport (" + I.fallbackTransport + ")")
                    } else {
                        af(false)
                    }
                } else {
                    if (I.transport === "sse") {
                        if (!M()) {
                            K("Server Side Events(SSE) is not supported, using request.fallbackTransport (" + I.fallbackTransport + ")")
                        } else {
                            C(false)
                        }
                    }
                }
            }
        }

        function ac(az) {
            var aC, aw, ay, ax = "atmosphere-" + az.url, av = {storage: function () {
                if (!jQuery.atmosphere.supportStorage()) {
                    return
                }
                var aF = window.localStorage, aD = function (aG) {
                    return jQuery.parseJSON(aF.getItem(ax + "-" + aG))
                }, aE = function (aG, aH) {
                    aF.setItem(ax + "-" + aG, jQuery.stringifyJSON(aH))
                };
                return{init: function () {
                    aE("children", aD("children").concat([D]));
                    jQuery(window).on("storage.socket", function (aG) {
                        aG = aG.originalEvent;
                        if (aG.key === ax && aG.newValue) {
                            aB(aG.newValue)
                        }
                    });
                    return aD("opened")
                }, signal: function (aG, aH) {
                    aF.setItem(ax, jQuery.stringifyJSON({target: "p", type: aG, data: aH}))
                }, close: function () {
                    var aG, aH = aD("children");
                    jQuery(window).off("storage.socket");
                    if (aH) {
                        aG = jQuery.inArray(az.id, aH);
                        if (aG > -1) {
                            aH.splice(aG, 1);
                            aE("children", aH)
                        }
                    }
                }}
            }, windowref: function () {
                var aD = window.open("", ax.replace(/\W/g, ""));
                if (!aD || aD.closed || !aD.callbacks) {
                    return
                }
                return{init: function () {
                    aD.callbacks.push(aB);
                    aD.children.push(D);
                    return aD.opened
                }, signal: function (aE, aF) {
                    if (!aD.closed && aD.fire) {
                        aD.fire(jQuery.stringifyJSON({target: "p", type: aE, data: aF}))
                    }
                }, close: function () {
                    function aE(aH, aG) {
                        var aF = jQuery.inArray(aG, aH);
                        if (aF > -1) {
                            aH.splice(aF, 1)
                        }
                    }

                    if (!ay) {
                        aE(aD.callbacks, aB);
                        aE(aD.children, D)
                    }
                }}
            }};

            function aB(aD) {
                var aF = jQuery.parseJSON(aD), aE = aF.data;
                if (aF.target === "c") {
                    switch (aF.type) {
                        case"open":
                            H("opening", "local", I);
                            break;
                        case"close":
                            if (!ay) {
                                ay = true;
                                if (aE.reason === "aborted") {
                                    ai()
                                } else {
                                    if (aE.heir === D) {
                                        n()
                                    } else {
                                        setTimeout(function () {
                                            n()
                                        }, 100)
                                    }
                                }
                            }
                            break;
                        case"message":
                            z(aE, "messageReceived", 200, az.transport);
                            break;
                        case"localMessage":
                            X(aE);
                            break
                    }
                }
            }

            function aA() {
                var aD = new RegExp("(?:^|; )(" + encodeURIComponent(ax) + ")=([^;]*)").exec(document.cookie);
                if (aD) {
                    return jQuery.parseJSON(decodeURIComponent(aD[2]))
                }
            }

            aC = aA();
            if (!aC || jQuery.now() - aC.ts > 1000) {
                return
            }
            aw = av.storage() || av.windowref();
            if (!aw) {
                return
            }
            return{open: function () {
                var aD;
                E = setInterval(function () {
                    var aE = aC;
                    aC = aA();
                    if (!aC || aE.ts === aC.ts) {
                        aB(jQuery.stringifyJSON({target: "c", type: "close", data: {reason: "error", heir: aE.heir}}))
                    }
                }, 1000);
                aD = aw.init();
                if (aD) {
                    setTimeout(function () {
                        H("opening", "local", az)
                    }, 50)
                }
                return aD
            }, send: function (aD) {
                aw.signal("send", aD)
            }, localSend: function (aD) {
                aw.signal("localSend", jQuery.stringifyJSON({id: D, event: aD}))
            }, close: function () {
                if (!aq) {
                    clearInterval(E);
                    aw.signal("close");
                    aw.close()
                }
            }}
        }

        function Y() {
            var aw, av = "atmosphere-" + I.url, aA = {storage: function () {
                if (!jQuery.atmosphere.supportStorage()) {
                    return
                }
                var aB = window.localStorage;
                return{init: function () {
                    jQuery(window).on("storage.socket", function (aC) {
                        aC = aC.originalEvent;
                        if (aC.key === av && aC.newValue) {
                            ax(aC.newValue)
                        }
                    })
                }, signal: function (aC, aD) {
                    aB.setItem(av, jQuery.stringifyJSON({target: "c", type: aC, data: aD}))
                }, get: function (aC) {
                    return jQuery.parseJSON(aB.getItem(av + "-" + aC))
                }, set: function (aC, aD) {
                    aB.setItem(av + "-" + aC, jQuery.stringifyJSON(aD))
                }, close: function () {
                    jQuery(window).off("storage.socket");
                    aB.removeItem(av);
                    aB.removeItem(av + "-opened");
                    aB.removeItem(av + "-children")
                }}
            }, windowref: function () {
                var aB = av.replace(/\W/g, ""), aC = (jQuery('iframe[name="' + aB + '"]')[0] || jQuery('<iframe name="' + aB + '" />').hide().appendTo("body")[0]).contentWindow;
                return{init: function () {
                    aC.callbacks = [ax];
                    aC.fire = function (aD) {
                        var aE;
                        for (aE = 0; aE < aC.callbacks.length; aE++) {
                            aC.callbacks[aE](aD)
                        }
                    }
                }, signal: function (aD, aE) {
                    if (!aC.closed && aC.fire) {
                        aC.fire(jQuery.stringifyJSON({target: "c", type: aD, data: aE}))
                    }
                }, get: function (aD) {
                    return !aC.closed ? aC[aD] : null
                }, set: function (aD, aE) {
                    if (!aC.closed) {
                        aC[aD] = aE
                    }
                }, close: function () {
                }}
            }};

            function ax(aB) {
                var aD = jQuery.parseJSON(aB), aC = aD.data;
                if (aD.target === "p") {
                    switch (aD.type) {
                        case"send":
                            ah(aC);
                            break;
                        case"localSend":
                            X(aC);
                            break;
                        case"close":
                            ai();
                            break
                    }
                }
            }

            U = function az(aB) {
                aw.signal("message", aB)
            };
            function ay() {
                document.cookie = au + "=" + encodeURIComponent(jQuery.stringifyJSON({ts: jQuery.now() + 1, heir: (aw.get("children") || [])[0]})) + "; path=/"
            }

            aw = aA.storage() || aA.windowref();
            aw.init();
            if (I.logLevel === "debug") {
                jQuery.atmosphere.debug("Installed StorageService " + aw)
            }
            aw.set("children", []);
            if (aw.get("opened") != null && !aw.get("opened")) {
                aw.set("opened", false)
            }
            au = encodeURIComponent(av);
            ay();
            E = setInterval(ay, 1000);
            al = aw
        }

        function H(ax, aA, aw) {
            if (I.shared && aA !== "local") {
                Y()
            }
            if (al != null) {
                al.set("opened", true)
            }
            aw.close = function () {
                ai()
            };
            if (f > 0 && ax === "re-connecting") {
                aw.isReopen = true;
                Z(Q)
            } else {
                if (Q.error == null) {
                    Q.request = aw;
                    var ay = Q.state;
                    Q.state = ax;
                    var av = Q.transport;
                    Q.transport = aA;
                    var az = Q.responseBody;
                    w();
                    Q.responseBody = az;
                    Q.state = ay;
                    Q.transport = av
                }
            }
        }

        function t(ax) {
            ax.transport = "jsonp";
            var aw = I;
            if ((ax != null) && (typeof(ax) !== "undefined")) {
                aw = ax
            }
            var av = aw.url;
            if (aw.dispatchUrl != null) {
                av += aw.dispatchUrl
            }
            var ay = aw.data;
            if (aw.attachHeadersAsQueryString) {
                av = R(aw);
                if (ay !== "") {
                    av += "&X-Atmosphere-Post-Body=" + encodeURIComponent(ay)
                }
                ay = ""
            }
            A = jQuery.ajax({url: av, type: aw.method, dataType: "jsonp", error: function (az, aB, aA) {
                Q.error = true;
                if (aw.openId) {
                    clearTimeout(aw.openId)
                }
                if (aw.reconnect && f++ < aw.maxReconnectOnClose) {
                    H("re-connecting", aw.transport, aw);
                    L(A, aw, aw.reconnectInterval);
                    aw.openId = setTimeout(function () {
                        aj(aw)
                    }, aw.reconnectInterval + 1000)
                } else {
                    aa(az.status, aA)
                }
            }, jsonp: "jsonpTransport", success: function (aA) {
                if (aw.reconnect) {
                    if (aw.maxRequest === -1 || aw.requestCount++ < aw.maxRequest) {
                        ab(A, aw);
                        if (!aw.executeCallbackBeforeReconnect) {
                            L(A, aw, 0)
                        }
                        var aC = aA.message;
                        if (aC != null && typeof aC !== "string") {
                            try {
                                aC = jQuery.stringifyJSON(aC)
                            } catch (aB) {
                            }
                        }
                        var az = r(aC, aw, Q);
                        if (!az) {
                            z(Q.responseBody, "messageReceived", 200, aw.transport)
                        }
                        if (aw.executeCallbackBeforeReconnect) {
                            L(A, aw, 0)
                        }
                    } else {
                        jQuery.atmosphere.log(I.logLevel, ["JSONP reconnect maximum try reached " + I.requestCount]);
                        aa(0, "maxRequest reached")
                    }
                }
            }, data: aw.data, beforeSend: function (az) {
                b(az, aw, false)
            }})
        }

        function V(ay) {
            var aw = I;
            if ((ay != null) && (typeof(ay) !== "undefined")) {
                aw = ay
            }
            var av = aw.url;
            if (aw.dispatchUrl != null) {
                av += aw.dispatchUrl
            }
            var az = aw.data;
            if (aw.attachHeadersAsQueryString) {
                av = R(aw);
                if (az !== "") {
                    av += "&X-Atmosphere-Post-Body=" + encodeURIComponent(az)
                }
                az = ""
            }
            var ax = typeof(aw.async) !== "undefined" ? aw.async : true;
            A = jQuery.ajax({url: av, type: aw.method, error: function (aA, aC, aB) {
                Q.error = true;
                if (aA.status < 300) {
                    L(A, aw)
                } else {
                    aa(aA.status, aB)
                }
            }, success: function (aC, aD, aB) {
                if (aw.reconnect) {
                    if (aw.maxRequest === -1 || aw.requestCount++ < aw.maxRequest) {
                        if (!aw.executeCallbackBeforeReconnect) {
                            L(A, aw, 0)
                        }
                        var aA = r(aC, aw, Q);
                        if (!aA) {
                            z(Q.responseBody, "messageReceived", 200, aw.transport)
                        }
                        if (aw.executeCallbackBeforeReconnect) {
                            L(A, aw, 0)
                        }
                    } else {
                        jQuery.atmosphere.log(I.logLevel, ["AJAX reconnect maximum try reached " + I.requestCount]);
                        aa(0, "maxRequest reached")
                    }
                }
            }, beforeSend: function (aA) {
                b(aA, aw, false)
            }, crossDomain: aw.enableXDR, async: ax})
        }

        function d(av) {
            if (I.webSocketImpl != null) {
                return I.webSocketImpl
            } else {
                if (window.WebSocket) {
                    return new WebSocket(av)
                } else {
                    return new MozWebSocket(av)
                }
            }
        }

        function e() {
            var av = R(I);
            return decodeURI(jQuery('<a href="' + av + '"/>')[0].href.replace(/^http/, "ws"))
        }

        function ar() {
            var av = R(I);
            return av
        }

        function C(aw) {
            Q.transport = "sse";
            var av = ar(I.url);
            if (I.logLevel === "debug") {
                jQuery.atmosphere.debug("Invoking executeSSE");
                jQuery.atmosphere.debug("Using URL: " + av)
            }
            if (I.enableProtocol && aw) {
                var ay = jQuery.now() - I.ctime;
                I.lastTimestamp = Number(I.stime) + Number(ay)
            }
            if (aw && !I.reconnect) {
                if (i != null) {
                    ag()
                }
                return
            }
            try {
                i = new EventSource(av, {withCredentials: I.withCredentials})
            } catch (ax) {
                aa(0, ax);
                K("SSE failed. Downgrading to fallback transport and resending");
                return
            }
            if (I.connectTimeout > 0) {
                I.id = setTimeout(function () {
                    if (!aw) {
                        ag()
                    }
                }, I.connectTimeout)
            }
            i.onopen = function (az) {
                s(I);
                if (I.logLevel === "debug") {
                    jQuery.atmosphere.debug("SSE successfully opened")
                }
                if (!I.enableProtocol) {
                    if (!aw) {
                        H("opening", "sse", I)
                    } else {
                        H("re-opening", "sse", I)
                    }
                } else {
                    if (I.isReopen) {
                        I.isReopen = false;
                        H("re-opening", I.transport, I)
                    }
                }
                aw = true;
                if (I.method === "POST") {
                    Q.state = "messageReceived";
                    i.send(I.data)
                }
            };
            i.onmessage = function (aA) {
                s(I);
                if (!I.enableXDR && aA.origin !== window.location.protocol + "//" + window.location.host) {
                    jQuery.atmosphere.log(I.logLevel, ["Origin was not " + window.location.protocol + "//" + window.location.host]);
                    return
                }
                Q.state = "messageReceived";
                Q.status = 200;
                aA = aA.data;
                var az = r(aA, I, Q);
                if (!az) {
                    w();
                    Q.responseBody = "";
                    Q.messages = []
                }
            };
            i.onerror = function (az) {
                clearTimeout(I.id);
                if (Q.closedByClientTimeout) {
                    return
                }
                ad(aw);
                ag();
                if (aq) {
                    jQuery.atmosphere.log(I.logLevel, ["SSE closed normally"])
                } else {
                    if (!aw) {
                        K("SSE failed. Downgrading to fallback transport and resending")
                    } else {
                        if (I.reconnect && (Q.transport === "sse")) {
                            if (f++ < I.maxReconnectOnClose) {
                                H("re-connecting", I.transport, I);
                                if (I.reconnectInterval > 0) {
                                    I.reconnectId = setTimeout(function () {
                                        C(true)
                                    }, I.reconnectInterval)
                                } else {
                                    C(true)
                                }
                                Q.responseBody = "";
                                Q.messages = []
                            } else {
                                jQuery.atmosphere.log(I.logLevel, ["SSE reconnect maximum try reached " + f]);
                                aa(0, "maxReconnectOnClose reached")
                            }
                        }
                    }
                }
            }
        }

        function af(aw) {
            Q.transport = "websocket";
            if (I.enableProtocol && aw) {
                var ax = jQuery.now() - I.ctime;
                I.lastTimestamp = Number(I.stime) + Number(ax)
            }
            var av = e(I.url);
            if (I.logLevel === "debug") {
                jQuery.atmosphere.debug("Invoking executeWebSocket");
                jQuery.atmosphere.debug("Using URL: " + av)
            }
            if (aw && !I.reconnect) {
                if (T != null) {
                    ag()
                }
                return
            }
            T = d(av);
            if (I.webSocketBinaryType != null) {
                T.binaryType = I.webSocketBinaryType
            }
            if (I.connectTimeout > 0) {
                I.id = setTimeout(function () {
                    if (!aw) {
                        var ay = {code: 1002, reason: "", wasClean: false};
                        T.onclose(ay);
                        try {
                            ag()
                        } catch (az) {
                        }
                        return
                    }
                }, I.connectTimeout)
            }
            T.onopen = function (az) {
                s(I);
                if (I.logLevel === "debug") {
                    jQuery.atmosphere.debug("Websocket successfully opened")
                }
                var ay = aw;
                aw = true;
                if (T != null) {
                    T.webSocketOpened = aw
                }
                if (!I.enableProtocol) {
                    if (ay) {
                        H("re-opening", "websocket", I)
                    } else {
                        H("opening", "websocket", I)
                    }
                }
                if (T != null) {
                    if (I.method === "POST") {
                        Q.state = "messageReceived";
                        T.send(I.data)
                    }
                }
            };
            T.onmessage = function (aA) {
                s(I);
                Q.state = "messageReceived";
                Q.status = 200;
                aA = aA.data;
                var ay = typeof(aA) === "string";
                if (ay) {
                    var az = r(aA, I, Q);
                    if (!az) {
                        w();
                        Q.responseBody = "";
                        Q.messages = []
                    }
                } else {
                    if (!o(I, aA)) {
                        return
                    }
                    Q.responseBody = aA;
                    w();
                    Q.responseBody = null
                }
            };
            T.onerror = function (ay) {
                clearTimeout(I.id)
            };
            T.onclose = function (ay) {
                if (Q.state === "closed") {
                    return
                }
                clearTimeout(I.id);
                var az = ay.reason;
                if (az === "") {
                    switch (ay.code) {
                        case 1000:
                            az = "Normal closure; the connection successfully completed whatever purpose for which it was created.";
                            break;
                        case 1001:
                            az = "The endpoint is going away, either because of a server failure or because the browser is navigating away from the page that opened the connection.";
                            break;
                        case 1002:
                            az = "The endpoint is terminating the connection due to a protocol error.";
                            break;
                        case 1003:
                            az = "The connection is being terminated because the endpoint received data of a type it cannot accept (for example, a text-only endpoint received binary data).";
                            break;
                        case 1004:
                            az = "The endpoint is terminating the connection because a data frame was received that is too large.";
                            break;
                        case 1005:
                            az = "Unknown: no status code was provided even though one was expected.";
                            break;
                        case 1006:
                            az = "Connection was closed abnormally (that is, with no close frame being sent).";
                            break
                    }
                }
                if (I.logLevel === "warn") {
                    jQuery.atmosphere.warn("Websocket closed, reason: " + az);
                    jQuery.atmosphere.warn("Websocket closed, wasClean: " + ay.wasClean)
                }
                if (Q.closedByClientTimeout) {
                    return
                }
                ad(aw);
                Q.state = "closed";
                if (aq) {
                    jQuery.atmosphere.log(I.logLevel, ["Websocket closed normally"])
                } else {
                    if (!aw) {
                        K("Websocket failed. Downgrading to Comet and resending")
                    } else {
                        if (I.reconnect && Q.transport === "websocket") {
                            ag();
                            if (f++ < I.maxReconnectOnClose) {
                                H("re-connecting", I.transport, I);
                                if (I.reconnectInterval > 0) {
                                    I.reconnectId = setTimeout(function () {
                                        Q.responseBody = "";
                                        Q.messages = [];
                                        af(true)
                                    }, I.reconnectInterval)
                                } else {
                                    Q.responseBody = "";
                                    Q.messages = [];
                                    af(true)
                                }
                            } else {
                                jQuery.atmosphere.log(I.logLevel, ["Websocket reconnect maximum try reached " + I.requestCount]);
                                if (I.logLevel === "warn") {
                                    jQuery.atmosphere.warn("Websocket error, reason: " + ay.reason)
                                }
                                aa(0, "maxReconnectOnClose reached")
                            }
                        }
                    }
                }
            };
            if (T.url === undefined) {
                T.onclose({reason: "Android 4.1 does not support websockets.", wasClean: false})
            }
        }

        function o(ay, ax) {
            var av = true;
            if (ay.transport === "polling") {
                return av
            }
            if (jQuery.trim(ax).length !== 0 && ay.enableProtocol && ay.firstMessage) {
                ay.firstMessage = false;
                var aw = ax.split(ay.messageDelimiter);
                var az = aw.length === 2 ? 0 : 1;
                ay.uuid = jQuery.trim(aw[az]);
                ay.stime = jQuery.trim(aw[az + 1]);
                av = false;
                if (ay.transport !== "long-polling") {
                    aj(ay)
                }
                jQuery.atmosphere.uuid = ay.uuid
            } else {
                if (ay.enableProtocol && ay.firstMessage) {
                    av = false
                } else {
                    aj(ay)
                }
            }
            return av
        }

        function s(av) {
            clearTimeout(av.id);
            if (av.timeout > 0 && av.transport !== "polling") {
                av.id = setTimeout(function () {
                    l(av);
                    x();
                    ag()
                }, av.timeout)
            }
        }

        function l(av) {
            Q.closedByClientTimeout = true;
            Q.state = "closedByClient";
            Q.responseBody = "";
            Q.status = 408;
            Q.messages = [];
            w()
        }

        function aa(av, aw) {
            ag();
            clearTimeout(I.id);
            Q.state = "error";
            Q.reasonPhrase = aw;
            Q.responseBody = "";
            Q.status = av;
            Q.messages = [];
            w()
        }

        function r(az, ay, av) {
            if (!o(ay, az)) {
                return true
            }
            if (az.length === 0) {
                return true
            }
            if (ay.trackMessageLength) {
                az = av.partialMessage + az;
                var ax = [];
                var aw = az.indexOf(ay.messageDelimiter);
                while (aw !== -1) {
                    var aB = jQuery.trim(az.substring(0, aw));
                    var aA = parseInt(aB, 10);
                    if (isNaN(aA)) {
                        throw'message length "' + aB + '" is not a number'
                    }
                    aw += ay.messageDelimiter.length;
                    if (aw + aA > az.length) {
                        aw = -1
                    } else {
                        ax.push(az.substring(aw, aw + aA));
                        az = az.substring(aw + aA, az.length);
                        aw = az.indexOf(ay.messageDelimiter)
                    }
                }
                av.partialMessage = az;
                if (ax.length !== 0) {
                    av.responseBody = ax.join(ay.messageDelimiter);
                    av.messages = ax;
                    return false
                } else {
                    av.responseBody = "";
                    av.messages = [];
                    return true
                }
            } else {
                av.responseBody = az
            }
            return false
        }

        function K(av) {
            jQuery.atmosphere.log(I.logLevel, [av]);
            if (typeof(I.onTransportFailure) !== "undefined") {
                I.onTransportFailure(av, I)
            } else {
                if (typeof(jQuery.atmosphere.onTransportFailure) !== "undefined") {
                    jQuery.atmosphere.onTransportFailure(av, I)
                }
            }
            I.transport = I.fallbackTransport;
            var aw = I.connectTimeout === -1 ? 0 : I.connectTimeout;
            if (I.reconnect && I.transport !== "none" || I.transport == null) {
                I.method = I.fallbackMethod;
                Q.transport = I.fallbackTransport;
                I.fallbackTransport = "none";
                if (aw > 0) {
                    I.reconnectId = setTimeout(function () {
                        n()
                    }, aw)
                } else {
                    n()
                }
            } else {
                aa(500, "Unable to reconnect with fallback transport")
            }
        }

        function R(ax, av) {
            var aw = I;
            if ((ax != null) && (typeof(ax) !== "undefined")) {
                aw = ax
            }
            if (av == null) {
                av = aw.url
            }
            if (!aw.attachHeadersAsQueryString) {
                return av
            }
            if (av.indexOf("X-Atmosphere-Framework") !== -1) {
                return av
            }
            av += (av.indexOf("?") !== -1) ? "&" : "?";
            av += "X-Atmosphere-tracking-id=" + aw.uuid;
            av += "&X-Atmosphere-Framework=" + jQuery.atmosphere.version;
            av += "&X-Atmosphere-Transport=" + aw.transport;
            if (aw.trackMessageLength) {
                av += "&X-Atmosphere-TrackMessageSize=true"
            }
            if (aw.lastTimestamp != null) {
                av += "&X-Cache-Date=" + aw.lastTimestamp
            } else {
                av += "&X-Cache-Date=" + 0
            }
            if (aw.contentType !== "") {
                av += "&Content-Type=" + aw.contentType
            }
            if (aw.enableProtocol) {
                av += "&X-atmo-protocol=true"
            }
            jQuery.each(aw.headers, function (ay, aA) {
                var az = jQuery.isFunction(aA) ? aA.call(this, aw, ax, Q) : aA;
                if (az != null) {
                    av += "&" + encodeURIComponent(ay) + "=" + encodeURIComponent(az)
                }
            });
            return av
        }

        function aj(av) {
            if (!av.isOpen) {
                av.isOpen = true;
                H("opening", av.transport, av)
            } else {
                if (av.isReopen) {
                    av.isReopen = false;
                    H("re-opening", av.transport, av)
                }
            }
        }

        function m(ax) {
            var av = I;
            if ((ax != null) || (typeof(ax) !== "undefined")) {
                av = ax
            }
            av.lastIndex = 0;
            av.readyState = 0;
            if ((av.transport === "jsonp") || ((av.enableXDR) && (jQuery.atmosphere.checkCORSSupport()))) {
                t(av);
                return
            }
            if (av.transport === "ajax") {
                V(ax);
                return
            }
            if (jQuery.browser.msie && jQuery.browser.version < 10) {
                if ((av.transport === "streaming")) {
                    if (av.enableXDR && window.XDomainRequest) {
                        J(av)
                    } else {
                        ap(av)
                    }
                    return
                }
                if ((av.enableXDR) && (window.XDomainRequest)) {
                    J(av);
                    return
                }
            }
            var ay = function () {
                av.lastIndex = 0;
                if (av.reconnect && f++ < av.maxReconnectOnClose) {
                    H("re-connecting", ax.transport, ax);
                    L(aw, av, ax.reconnectInterval)
                } else {
                    aa(0, "maxReconnectOnClose reached")
                }
            };
            if (av.reconnect && (av.maxRequest === -1 || av.requestCount++ < av.maxRequest)) {
                var aw = jQuery.ajaxSettings.xhr();
                aw.hasData = false;
                b(aw, av, true);
                if (av.suspend) {
                    p = aw
                }
                if (av.transport !== "polling") {
                    Q.transport = av.transport;
                    aw.onabort = function () {
                        ad(true)
                    };
                    aw.onerror = function () {
                        Q.error = true;
                        try {
                            Q.status = XMLHttpRequest.status
                        } catch (az) {
                            Q.status = 500
                        }
                        if (!Q.status) {
                            Q.status = 500
                        }
                        if (!Q.errorHandled) {
                            ag();
                            ay()
                        }
                    }
                }
                aw.onreadystatechange = function () {
                    if (aq) {
                        return
                    }
                    Q.error = null;
                    var aA = false;
                    var aF = false;
                    if (av.transport === "streaming" && av.readyState > 2 && aw.readyState === 4) {
                        ag();
                        ay();
                        return
                    }
                    av.readyState = aw.readyState;
                    if (av.transport === "streaming" && aw.readyState >= 3) {
                        aF = true
                    } else {
                        if (av.transport === "long-polling" && aw.readyState === 4) {
                            aF = true
                        }
                    }
                    s(I);
                    if (av.transport !== "polling") {
                        var az = 200;
                        if (aw.readyState === 4) {
                            az = aw.status > 1000 ? 0 : aw.status
                        }
                        if (az >= 300 || az === 0) {
                            Q.errorHandled = true;
                            ag();
                            ay();
                            return
                        }
                        if ((!av.enableProtocol || !ax.firstMessage) && aw.readyState === 2) {
                            aj(av)
                        }
                    } else {
                        if (aw.readyState === 4) {
                            aF = true
                        }
                    }
                    if (aF) {
                        var aD = aw.responseText;
                        if (jQuery.trim(aD.length).length === 0 && av.transport === "long-polling") {
                            if (!aw.hasData) {
                                ay()
                            } else {
                                aw.hasData = false
                            }
                            return
                        }
                        aw.hasData = true;
                        ab(aw, I);
                        if (av.transport === "streaming") {
                            if (!jQuery.browser.opera) {
                                var aC = aD.substring(av.lastIndex, aD.length);
                                aA = r(aC, av, Q);
                                av.lastIndex = aD.length;
                                if (aA) {
                                    return
                                }
                            } else {
                                jQuery.atmosphere.iterate(function () {
                                    if (Q.status !== 500 && aw.responseText.length > av.lastIndex) {
                                        try {
                                            Q.status = aw.status;
                                            Q.headers = a(aw.getAllResponseHeaders());
                                            ab(aw, I)
                                        } catch (aH) {
                                            Q.status = 404
                                        }
                                        s(I);
                                        Q.state = "messageReceived";
                                        var aG = aw.responseText.substring(av.lastIndex);
                                        av.lastIndex = aw.responseText.length;
                                        aA = r(aG, av, Q);
                                        if (!aA) {
                                            w()
                                        }
                                        F(aw, av)
                                    } else {
                                        if (Q.status > 400) {
                                            av.lastIndex = aw.responseText.length;
                                            return false
                                        }
                                    }
                                }, 0)
                            }
                        } else {
                            aA = r(aD, av, Q)
                        }
                        try {
                            Q.status = aw.status;
                            Q.headers = a(aw.getAllResponseHeaders());
                            ab(aw, av)
                        } catch (aE) {
                            Q.status = 404
                        }
                        if (av.suspend) {
                            Q.state = Q.status === 0 ? "closed" : "messageReceived"
                        } else {
                            Q.state = "messagePublished"
                        }
                        var aB = ax.transport !== "streaming" && ax.transport !== "polling";
                        if (aB && !av.executeCallbackBeforeReconnect) {
                            L(aw, av, 0)
                        }
                        if (Q.responseBody.length !== 0 && !aA) {
                            w()
                        }
                        if (aB && av.executeCallbackBeforeReconnect) {
                            L(aw, av, 0)
                        }
                        F(aw, av)
                    }
                };
                aw.send(av.data);
                ae = true
            } else {
                if (av.logLevel === "debug") {
                    jQuery.atmosphere.log(av.logLevel, ["Max re-connection reached."])
                }
                aa(0, "maxRequest reached")
            }
        }

        function b(ax, ay, aw) {
            var av = ay.url;
            if (ay.dispatchUrl != null && ay.method === "POST") {
                av += ay.dispatchUrl
            }
            av = R(ay, av);
            av = jQuery.atmosphere.prepareURL(av);
            if (aw) {
                ax.open(ay.method, av, true);
                if (ay.connectTimeout > 0) {
                    ay.id = setTimeout(function () {
                        if (ay.requestCount === 0) {
                            ag();
                            z("Connect timeout", "closed", 200, ay.transport)
                        }
                    }, ay.connectTimeout)
                }
            }
            if (I.withCredentials) {
                if ("withCredentials" in ax) {
                    ax.withCredentials = true
                }
            }
            if (!I.dropAtmosphereHeaders) {
                ax.setRequestHeader("X-Atmosphere-Framework", jQuery.atmosphere.version);
                ax.setRequestHeader("X-Atmosphere-Transport", ay.transport);
                if (ay.lastTimestamp != null) {
                    ax.setRequestHeader("X-Cache-Date", ay.lastTimestamp)
                } else {
                    ax.setRequestHeader("X-Cache-Date", 0)
                }
                if (ay.trackMessageLength) {
                    ax.setRequestHeader("X-Atmosphere-TrackMessageSize", "true")
                }
                ax.setRequestHeader("X-Atmosphere-tracking-id", ay.uuid)
            }
            if (ay.contentType !== "") {
                ax.setRequestHeader("Content-Type", ay.contentType)
            }
            jQuery.each(ay.headers, function (az, aB) {
                var aA = jQuery.isFunction(aB) ? aB.call(this, ax, ay, aw, Q) : aB;
                if (aA != null) {
                    ax.setRequestHeader(az, aA)
                }
            })
        }

        function L(aw, ax, ay) {
            if (ax.reconnect || (ax.suspend && ae)) {
                var av = 0;
                if (aw.readyState !== 0) {
                    av = aw.status > 1000 ? 0 : aw.status
                }
                Q.status = av === 0 ? 204 : av;
                Q.reason = av === 0 ? "Server resumed the connection or down." : "OK";
                clearTimeout(ax.id);
                if (ax.reconnectId) {
                    clearTimeout(ax.reconnectId)
                }
                if (ay > 0) {
                    setTimeout(function () {
                        I.reconnectId = m(ax)
                    }, ay)
                } else {
                    m(ax)
                }
            }
        }

        function Z(av) {
            av.state = "re-connecting";
            W(av)
        }

        function J(av) {
            if (av.transport !== "polling") {
                y = P(av);
                y.open()
            } else {
                P(av).open()
            }
        }

        function P(ax) {
            var aw = I;
            if ((ax != null) && (typeof(ax) !== "undefined")) {
                aw = ax
            }
            var aC = aw.transport;
            var aB = 0;
            var av = new window.XDomainRequest();
            var az = function () {
                if (aw.transport === "long-polling" && (aw.reconnect && (aw.maxRequest === -1 || aw.requestCount++ < aw.maxRequest))) {
                    av.status = 200;
                    J(aw)
                }
            };
            var aA = aw.rewriteURL || function (aE) {
                var aD = /(?:^|;\s*)(JSESSIONID|PHPSESSID)=([^;]*)/.exec(document.cookie);
                switch (aD && aD[1]) {
                    case"JSESSIONID":
                        return aE.replace(/;jsessionid=[^\?]*|(\?)|$/, ";jsessionid=" + aD[2] + "$1");
                    case"PHPSESSID":
                        return aE.replace(/\?PHPSESSID=[^&]*&?|\?|$/, "?PHPSESSID=" + aD[2] + "&").replace(/&$/, "")
                }
                return aE
            };
            av.onprogress = function () {
                ay(av)
            };
            av.onerror = function () {
                if (aw.transport !== "polling") {
                    ag();
                    if (f++ < aw.maxReconnectOnClose) {
                        if (aw.reconnectInterval > 0) {
                            aw.reconnectId = setTimeout(function () {
                                H("re-connecting", ax.transport, ax);
                                J(aw)
                            }, aw.reconnectInterval)
                        } else {
                            H("re-connecting", ax.transport, ax);
                            J(aw)
                        }
                    } else {
                        aa(0, "maxReconnectOnClose reached")
                    }
                }
            };
            av.onload = function () {
            };
            var ay = function (aD) {
                clearTimeout(aw.id);
                var aF = aD.responseText;
                aF = aF.substring(aB);
                aB += aF.length;
                if (aC !== "polling") {
                    s(aw);
                    var aE = r(aF, aw, Q);
                    if (aC === "long-polling" && jQuery.trim(aF).length === 0) {
                        return
                    }
                    if (aw.executeCallbackBeforeReconnect) {
                        az()
                    }
                    if (!aE) {
                        z(Q.responseBody, "messageReceived", 200, aC)
                    }
                    if (!aw.executeCallbackBeforeReconnect) {
                        az()
                    }
                }
            };
            return{open: function () {
                var aD = aw.url;
                if (aw.dispatchUrl != null) {
                    aD += aw.dispatchUrl
                }
                aD = R(aw, aD);
                av.open(aw.method, aA(aD));
                if (aw.method === "GET") {
                    av.send()
                } else {
                    av.send(aw.data)
                }
                if (aw.connectTimeout > 0) {
                    aw.id = setTimeout(function () {
                        if (aw.requestCount === 0) {
                            ag();
                            z("Connect timeout", "closed", 200, aw.transport)
                        }
                    }, aw.connectTimeout)
                }
            }, close: function () {
                av.abort()
            }}
        }

        function ap(av) {
            y = q(av);
            y.open()
        }

        function q(ay) {
            var ax = I;
            if ((ay != null) && (typeof(ay) !== "undefined")) {
                ax = ay
            }
            var aw;
            var az = new window.ActiveXObject("htmlfile");
            az.open();
            az.close();
            var av = ax.url;
            if (ax.dispatchUrl != null) {
                av += ax.dispatchUrl
            }
            if (ax.transport !== "polling") {
                Q.transport = ax.transport
            }
            return{open: function () {
                var aA = az.createElement("iframe");
                av = R(ax);
                if (ax.data !== "") {
                    av += "&X-Atmosphere-Post-Body=" + encodeURIComponent(ax.data)
                }
                av = jQuery.atmosphere.prepareURL(av);
                aA.src = av;
                az.body.appendChild(aA);
                var aB = aA.contentDocument || aA.contentWindow.document;
                aw = jQuery.atmosphere.iterate(function () {
                    try {
                        if (!aB.firstChild) {
                            return
                        }
                        if (aB.readyState === "complete") {
                            try {
                                jQuery.noop(aB.fileSize)
                            } catch (aH) {
                                z("Connection Failure", "error", 500, ax.transport);
                                return false
                            }
                        }
                        var aE = aB.body ? aB.body.lastChild : aB;
                        var aG = function () {
                            var aJ = aE.cloneNode(true);
                            aJ.appendChild(aB.createTextNode("."));
                            var aI = aJ.innerText;
                            aI = aI.substring(0, aI.length - 1);
                            return aI
                        };
                        if (!jQuery.nodeName(aE, "pre")) {
                            var aD = aB.head || aB.getElementsByTagName("head")[0] || aB.documentElement || aB;
                            var aC = aB.createElement("script");
                            aC.text = "document.write('<plaintext>')";
                            aD.insertBefore(aC, aD.firstChild);
                            aD.removeChild(aC);
                            aE = aB.body.lastChild
                        }
                        if (ax.closed) {
                            ax.isReopen = true
                        }
                        aw = jQuery.atmosphere.iterate(function () {
                            var aJ = aG();
                            if (aJ.length > ax.lastIndex) {
                                s(I);
                                Q.status = 200;
                                Q.error = null;
                                aE.innerText = "";
                                var aI = r(aJ, ax, Q);
                                if (aI) {
                                    return""
                                }
                                z(Q.responseBody, "messageReceived", 200, ax.transport)
                            }
                            ax.lastIndex = 0;
                            if (aB.readyState === "complete") {
                                ad(true);
                                H("re-connecting", ax.transport, ax);
                                if (ax.reconnectInterval > 0) {
                                    ax.reconnectId = setTimeout(function () {
                                        ap(ax)
                                    }, ax.reconnectInterval)
                                } else {
                                    ap(ax)
                                }
                                return false
                            }
                        }, null);
                        return false
                    } catch (aF) {
                        Q.error = true;
                        H("re-connecting", ax.transport, ax);
                        if (f++ < ax.maxReconnectOnClose) {
                            if (ax.reconnectInterval > 0) {
                                ax.reconnectId = setTimeout(function () {
                                    ap(ax)
                                }, ax.reconnectInterval)
                            } else {
                                ap(ax)
                            }
                        } else {
                            aa(0, "maxReconnectOnClose reached")
                        }
                        az.execCommand("Stop");
                        az.close();
                        return false
                    }
                })
            }, close: function () {
                if (aw) {
                    aw()
                }
                az.execCommand("Stop");
                ad(true)
            }}
        }

        function ah(av) {
            if (k != null) {
                g(av)
            } else {
                if (p != null || i != null) {
                    c(av)
                } else {
                    if (y != null) {
                        S(av)
                    } else {
                        if (A != null) {
                            O(av)
                        } else {
                            if (T != null) {
                                B(av)
                            } else {
                                aa(0, "No suspended connection available");
                                jQuery.atmosphere.error("No suspended connection available. Make sure atmosphere.subscribe has been called and request.onOpen invoked before invoking this method")
                            }
                        }
                    }
                }
            }
        }

        function h(aw) {
            var av = ak(aw);
            av.transport = "ajax";
            av.method = "GET";
            av.async = false;
            av.reconnect = false;
            m(av)
        }

        function g(av) {
            k.send(av)
        }

        function v(aw) {
            if (aw.length === 0) {
                return
            }
            try {
                if (k) {
                    k.localSend(aw)
                } else {
                    if (al) {
                        al.signal("localMessage", jQuery.stringifyJSON({id: D, event: aw}))
                    }
                }
            } catch (av) {
                jQuery.atmosphere.error(av)
            }
        }

        function c(aw) {
            var av = ak(aw);
            m(av)
        }

        function S(aw) {
            if (I.enableXDR && jQuery.atmosphere.checkCORSSupport()) {
                var av = ak(aw);
                av.reconnect = false;
                t(av)
            } else {
                c(aw)
            }
        }

        function O(av) {
            c(av)
        }

        function N(av) {
            var aw = av;
            if (typeof(aw) === "object") {
                aw = av.data
            }
            return aw
        }

        function ak(aw) {
            var ax = N(aw);
            var av = {connected: false, timeout: 60000, method: "POST", url: I.url, contentType: I.contentType, headers: I.headers, reconnect: true, callback: null, data: ax, suspend: false, maxRequest: -1, logLevel: "info", requestCount: 0, withCredentials: I.withCredentials, transport: "polling", isOpen: true, attachHeadersAsQueryString: true, enableXDR: I.enableXDR, uuid: I.uuid, dispatchUrl: I.dispatchUrl, enableProtocol: false, messageDelimiter: "|", maxReconnectOnClose: I.maxReconnectOnClose};
            if (typeof(aw) === "object") {
                av = jQuery.extend(av, aw)
            }
            return av
        }

        function B(av) {
            var ay = jQuery.atmosphere.isBinary(av) ? av : N(av);
            var aw;
            try {
                if (I.dispatchUrl != null) {
                    aw = I.webSocketPathDelimiter + I.dispatchUrl + I.webSocketPathDelimiter + ay
                } else {
                    aw = ay
                }
                if (!T.webSocketOpened) {
                    jQuery.atmosphere.error("WebSocket not connected.");
                    return
                }
                T.send(aw)
            } catch (ax) {
                T.onclose = function (az) {
                };
                ag();
                K("Websocket failed. Downgrading to Comet and resending " + aw);
                c(av)
            }
        }

        function X(aw) {
            var av = jQuery.parseJSON(aw);
            if (av.id !== D) {
                if (typeof(I.onLocalMessage) !== "undefined") {
                    I.onLocalMessage(av.event)
                } else {
                    if (typeof(jQuery.atmosphere.onLocalMessage) !== "undefined") {
                        jQuery.atmosphere.onLocalMessage(av.event)
                    }
                }
            }
        }

        function z(ay, av, aw, ax) {
            Q.responseBody = ay;
            Q.transport = ax;
            Q.status = aw;
            Q.state = av;
            w()
        }

        function ab(av, ay) {
            if (!ay.readResponsesHeaders && !ay.enableProtocol) {
                ay.lastTimestamp = jQuery.now();
                ay.uuid = jQuery.atmosphere.guid();
                return
            }
            try {
                var ax = av.getResponseHeader("X-Cache-Date");
                if (ax && ax != null && ax.length > 0) {
                    ay.lastTimestamp = ax.split(" ").pop()
                }
                var aw = av.getResponseHeader("X-Atmosphere-tracking-id");
                if (aw && aw != null) {
                    ay.uuid = aw.split(" ").pop()
                }
                if (ay.headers) {
                    jQuery.each(I.headers, function (aB) {
                        var aA = av.getResponseHeader(aB);
                        if (aA) {
                            Q.headers[aB] = aA
                        }
                    })
                }
            } catch (az) {
            }
        }

        function W(av) {
            ao(av, I);
            ao(av, jQuery.atmosphere)
        }

        function ao(aw, ax) {
            switch (aw.state) {
                case"messageReceived":
                    f = 0;
                    if (typeof(ax.onMessage) !== "undefined") {
                        ax.onMessage(aw)
                    }
                    break;
                case"error":
                    if (typeof(ax.onError) !== "undefined") {
                        ax.onError(aw)
                    }
                    break;
                case"opening":
                    delete I.closed;
                    if (typeof(ax.onOpen) !== "undefined") {
                        ax.onOpen(aw)
                    }
                    break;
                case"messagePublished":
                    if (typeof(ax.onMessagePublished) !== "undefined") {
                        ax.onMessagePublished(aw)
                    }
                    break;
                case"re-connecting":
                    if (typeof(ax.onReconnect) !== "undefined") {
                        ax.onReconnect(I, aw)
                    }
                    break;
                case"closedByClient":
                    if (typeof(ax.onClientTimeout) !== "undefined") {
                        ax.onClientTimeout(I)
                    }
                    break;
                case"re-opening":
                    delete I.closed;
                    if (typeof(ax.onReopen) !== "undefined") {
                        ax.onReopen(I, aw)
                    }
                    break;
                case"fail-to-reconnect":
                    if (typeof(ax.onFailureToReconnect) !== "undefined") {
                        ax.onFailureToReconnect(I, aw)
                    }
                    break;
                case"unsubscribe":
                case"closed":
                    var av = typeof(I.closed) !== "undefined" ? I.closed : false;
                    if (typeof(ax.onClose) !== "undefined" && !av) {
                        ax.onClose(aw)
                    }
                    I.closed = true;
                    break
            }
        }

        function ad(av) {
            if (Q.state !== "closed") {
                Q.state = "closed";
                Q.responseBody = "";
                Q.messages = [];
                Q.status = !av ? 501 : 200;
                w()
            }
        }

        function w() {
            var ax = function (aA, aB) {
                aB(Q)
            };
            if (k == null && U != null) {
                U(Q.responseBody)
            }
            I.reconnect = I.mrequest;
            var av = typeof(Q.responseBody) === "string";
            var ay = (av && I.trackMessageLength) ? (Q.messages.length > 0 ? Q.messages : [""]) : new Array(Q.responseBody);
            for (var aw = 0; aw < ay.length; aw++) {
                if (ay.length > 1 && ay[aw].length === 0) {
                    continue
                }
                Q.responseBody = (av) ? jQuery.trim(ay[aw]) : ay[aw];
                if (k == null && U != null) {
                    U(Q.responseBody)
                }
                if (Q.responseBody.length === 0 && Q.state === "messageReceived") {
                    continue
                }
                W(Q);
                if (jQuery.atmosphere.callbacks.length > 0) {
                    if (I.logLevel === "debug") {
                        jQuery.atmosphere.debug("Invoking " + jQuery.atmosphere.callbacks.length + " global callbacks: " + Q.state)
                    }
                    try {
                        jQuery.each(jQuery.atmosphere.callbacks, ax)
                    } catch (az) {
                        jQuery.atmosphere.log(I.logLevel, ["Callback exception" + az])
                    }
                }
                if (typeof(I.callback) === "function") {
                    if (I.logLevel === "debug") {
                        jQuery.atmosphere.debug("Invoking request callbacks")
                    }
                    try {
                        I.callback(Q)
                    } catch (az) {
                        jQuery.atmosphere.log(I.logLevel, ["Callback exception" + az])
                    }
                }
            }
        }

        function F(aw, av) {
            if (Q.partialMessage === "" && (av.transport === "streaming") && (aw.responseText.length > av.maxStreamingLength)) {
                Q.messages = [];
                ad(true);
                x();
                ag();
                L(aw, av, 0)
            }
        }

        function x() {
            if (I.enableProtocol && !I.firstMessage) {
                var aw = "X-Atmosphere-Transport=close&X-Atmosphere-tracking-id=" + I.uuid;
                jQuery.each(I.headers, function (ax, az) {
                    var ay = jQuery.isFunction(az) ? az.call(this, I, I, Q) : az;
                    if (ay != null) {
                        aw += "&" + encodeURIComponent(ax) + "=" + encodeURIComponent(ay)
                    }
                });
                var av = I.url.replace(/([?&])_=[^&]*/, aw);
                av = av + (av === I.url ? (/\?/.test(I.url) ? "&" : "?") + aw : "");
                if (I.connectTimeout > 0) {
                    jQuery.ajax({url: av, async: true, timeout: I.connectTimeout, cache: false})
                } else {
                    jQuery.ajax({url: av, async: true, cache: false})
                }
            }
        }

        function ai() {
            if (I.reconnectId) {
                clearTimeout(I.reconnectId)
            }
            I.reconnect = false;
            aq = true;
            Q.request = I;
            Q.state = "unsubscribe";
            Q.responseBody = "";
            Q.status = 408;
            w();
            x();
            ag()
        }

        function ag() {
            if (I.id) {
                clearTimeout(I.id)
            }
            if (y != null) {
                y.close();
                y = null
            }
            if (A != null) {
                A.abort();
                A = null
            }
            if (p != null) {
                p.abort();
                p = null
            }
            if (T != null) {
                if (T.webSocketOpened) {
                    T.close()
                }
                T = null
            }
            if (i != null) {
                i.close();
                i = null
            }
            an()
        }

        function an() {
            if (al != null) {
                clearInterval(E);
                document.cookie = au + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
                al.signal("close", {reason: "", heir: !aq ? D : (al.get("children") || [])[0]});
                al.close()
            }
            if (k != null) {
                k.close()
            }
        }

        this.subscribe = function (av) {
            at(av);
            n()
        };
        this.execute = function () {
            n()
        };
        this.invokeCallback = function () {
            w()
        };
        this.close = function () {
            ai()
        };
        this.disconnect = function () {
            x()
        };
        this.getUrl = function () {
            return I.url
        };
        this.push = function (ax, aw) {
            if (aw != null) {
                var av = I.dispatchUrl;
                I.dispatchUrl = aw;
                ah(ax);
                I.dispatchUrl = av
            } else {
                ah(ax)
            }
        };
        this.getUUID = function () {
            return I.uuid
        };
        this.pushLocal = function (av) {
            v(av)
        };
        this.enableProtocol = function (av) {
            return I.enableProtocol
        };
        this.request = I;
        this.response = Q
    }, subscribe: function (b, e, d) {
        if (typeof(e) === "function") {
            jQuery.atmosphere.addCallback(e)
        }
        if (typeof(b) !== "string") {
            d = b
        } else {
            d.url = b
        }
        var c = new jQuery.atmosphere.AtmosphereRequest(d);
        c.execute();
        jQuery.atmosphere.requests[jQuery.atmosphere.requests.length] = c;
        return c
    }, addCallback: function (b) {
        if (jQuery.inArray(b, jQuery.atmosphere.callbacks) === -1) {
            jQuery.atmosphere.callbacks.push(b)
        }
    }, removeCallback: function (c) {
        var b = jQuery.inArray(c, jQuery.atmosphere.callbacks);
        if (b !== -1) {
            jQuery.atmosphere.callbacks.splice(b, 1)
        }
    }, unsubscribe: function () {
        if (jQuery.atmosphere.requests.length > 0) {
            var b = [].concat(jQuery.atmosphere.requests);
            for (var d = 0; d < b.length; d++) {
                var c = b[d];
                c.close();
                clearTimeout(c.response.request.id)
            }
        }
        jQuery.atmosphere.requests = [];
        jQuery.atmosphere.callbacks = []
    }, unsubscribeUrl: function (c) {
        var b = -1;
        if (jQuery.atmosphere.requests.length > 0) {
            for (var e = 0; e < jQuery.atmosphere.requests.length; e++) {
                var d = jQuery.atmosphere.requests[e];
                if (d.getUrl() === c) {
                    d.close();
                    clearTimeout(d.response.request.id);
                    b = e;
                    break
                }
            }
        }
        if (b >= 0) {
            jQuery.atmosphere.requests.splice(b, 1)
        }
    }, publish: function (c) {
        if (typeof(c.callback) === "function") {
            jQuery.atmosphere.addCallback(c.callback)
        }
        c.transport = "polling";
        var b = new jQuery.atmosphere.AtmosphereRequest(c);
        jQuery.atmosphere.requests[jQuery.atmosphere.requests.length] = b;
        return b
    }, checkCORSSupport: function () {
        if (jQuery.browser.msie && !window.XDomainRequest) {
            return true
        } else {
            if (jQuery.browser.opera && jQuery.browser.version < 12) {
                return true
            } else {
                if (jQuery.trim(navigator.userAgent).slice(0, 16) === "KreaTVWebKit/531") {
                    return true
                } else {
                    if (jQuery.trim(navigator.userAgent).slice(-7).toLowerCase() === "kreatel") {
                        return true
                    }
                }
            }
        }
        var b = navigator.userAgent.toLowerCase();
        var c = b.indexOf("android") > -1;
        if (c) {
            return true
        }
        return false
    }, S4: function () {
        return(((1 + Math.random()) * 65536) | 0).toString(16).substring(1)
    }, guid: function () {
        return(jQuery.atmosphere.S4() + jQuery.atmosphere.S4() + "-" + jQuery.atmosphere.S4() + "-" + jQuery.atmosphere.S4() + "-" + jQuery.atmosphere.S4() + "-" + jQuery.atmosphere.S4() + jQuery.atmosphere.S4() + jQuery.atmosphere.S4())
    }, prepareURL: function (c) {
        var d = jQuery.now();
        var b = c.replace(/([?&])_=[^&]*/, "$1_=" + d);
        return b + (b === c ? (/\?/.test(c) ? "&" : "?") + "_=" + d : "")
    }, param: function (b) {
        return jQuery.param(b, jQuery.ajaxSettings.traditional)
    }, supportStorage: function () {
        var c = window.localStorage;
        if (c) {
            try {
                c.setItem("t", "t");
                c.removeItem("t");
                return window.StorageEvent && !jQuery.browser.msie && !(jQuery.browser.mozilla && jQuery.browser.version.split(".")[0] === "1")
            } catch (b) {
            }
        }
        return false
    }, iterate: function (d, c) {
        var e;
        c = c || 0;
        (function b() {
            e = setTimeout(function () {
                if (d() === false) {
                    return
                }
                b()
            }, c)
        })();
        return function () {
            clearTimeout(e)
        }
    }, log: function (d, c) {
        if (window.console) {
            var b = window.console[d];
            if (typeof b === "function") {
                b.apply(window.console, c)
            }
        }
    }, warn: function () {
        jQuery.atmosphere.log("warn", arguments)
    }, info: function () {
        jQuery.atmosphere.log("info", arguments)
    }, debug: function () {
        jQuery.atmosphere.log("debug", arguments)
    }, error: function () {
        jQuery.atmosphere.log("error", arguments)
    }, isBinary: function (b) {
        return/^\[object\s(?:Blob|ArrayBuffer|.+Array)\]$/.test(Object.prototype.toString.call(b))
    }}
}();
(function () {
    var a, b;
    jQuery.uaMatch = function (d) {
        d = d.toLowerCase();
        var c = /(chrome)[ \/]([\w.]+)/.exec(d) || /(webkit)[ \/]([\w.]+)/.exec(d) || /(opera)(?:.*version|)[ \/]([\w.]+)/.exec(d) || /(msie) ([\w.]+)/.exec(d) || d.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec(d) || [];
        return{browser: c[1] || "", version: c[2] || "0"}
    };
    a = jQuery.uaMatch(navigator.userAgent);
    b = {};
    if (a.browser) {
        b[a.browser] = true;
        b.version = a.version
    }
    if (b.chrome) {
        b.webkit = true
    } else {
        if (b.webkit) {
            b.safari = true
        }
    }
    jQuery.browser = b;
    jQuery.sub = function () {
        function c(f, g) {
            return new c.fn.init(f, g)
        }

        jQuery.extend(true, c, this);
        c.superclass = this;
        c.fn = c.prototype = this();
        c.fn.constructor = c;
        c.sub = this.sub;
        c.fn.init = function e(f, g) {
            if (g && g instanceof jQuery && !(g instanceof c)) {
                g = c(g)
            }
            return jQuery.fn.init.call(this, f, g, d)
        };
        c.fn.init.prototype = c.fn;
        var d = c(document);
        return c
    }
})();
(function (d) {
    var g = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, c = {"\b": "\\b", "\t": "\\t", "\n": "\\n", "\f": "\\f", "\r": "\\r", '"': '\\"', "\\": "\\\\"};

    function a(f) {
        return'"' + f.replace(g, function (h) {
            var i = c[h];
            return typeof i === "string" ? i : "\\u" + ("0000" + h.charCodeAt(0).toString(16)).slice(-4)
        }) + '"'
    }

    function b(f) {
        return f < 10 ? "0" + f : f
    }

    function e(m, l) {
        var k, j, f, h, o = l[m], n = typeof o;
        if (o && typeof o === "object" && typeof o.toJSON === "function") {
            o = o.toJSON(m);
            n = typeof o
        }
        switch (n) {
            case"string":
                return a(o);
            case"number":
                return isFinite(o) ? String(o) : "null";
            case"boolean":
                return String(o);
            case"object":
                if (!o) {
                    return"null"
                }
                switch (Object.prototype.toString.call(o)) {
                    case"[object Date]":
                        return isFinite(o.valueOf()) ? '"' + o.getUTCFullYear() + "-" + b(o.getUTCMonth() + 1) + "-" + b(o.getUTCDate()) + "T" + b(o.getUTCHours()) + ":" + b(o.getUTCMinutes()) + ":" + b(o.getUTCSeconds()) + 'Z"' : "null";
                    case"[object Array]":
                        f = o.length;
                        h = [];
                        for (k = 0; k < f; k++) {
                            h.push(e(k, o) || "null")
                        }
                        return"[" + h.join(",") + "]";
                    default:
                        h = [];
                        for (k in o) {
                            if (Object.prototype.hasOwnProperty.call(o, k)) {
                                j = e(k, o);
                                if (j) {
                                    h.push(a(k) + ":" + j)
                                }
                            }
                        }
                        return"{" + h.join(",") + "}"
                }
        }
    }

    d.stringifyJSON = function (f) {
        if (window.JSON && window.JSON.stringify) {
            return window.JSON.stringify(f)
        }
        return e("", {"": f})
    }
}(jQuery));