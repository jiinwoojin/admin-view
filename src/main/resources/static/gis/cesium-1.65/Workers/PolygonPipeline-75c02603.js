/**
 * Cesium - https://github.com/AnalyticalGraphicsInc/cesium
 *
 * Copyright 2011-2017 Cesium Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Columbus View (Pat. Pend.)
 *
 * Portions licensed separately.
 * See https://github.com/AnalyticalGraphicsInc/cesium/blob/master/LICENSE.md for full licensing details.
 */
define(["exports","./defined-2a4f2d00","./Check-e5651467","./freezeObject-a51e076f","./defaultValue-29c9b1af","./Math-7782f09e","./Cartesian2-ba70b51f","./WebGLConstants-90dbfe2f","./ComponentDatatype-418b1c61","./GeometryAttribute-75811f09","./EllipsoidRhumbLine-d5a5f3d0"],function(e,T,t,n,W,P,I,r,B,N,U){"use strict";function a(e,t,n){n=n||2;var r,a,i,u,x,o,s,p=t&&t.length,f=p?t[0]*n:e.length,h=y(e,0,f,n,!0),l=[];if(!h)return l;if(p&&(h=function(e,t,n,r){var a,i,u,x,o,s=[];for(a=0,i=t.length;a<i;a++)u=t[a]*r,x=a<i-1?t[a+1]*r:e.length,(o=y(e,u,x,r,!1))===o.next&&(o.steiner=!0),s.push(g(o));for(s.sort(C),a=0;a<s.length;a++)m(s[a],n),n=c(n,n.next);return n}(e,t,h,n)),e.length>80*n){r=i=e[0],a=u=e[1];for(var v=n;v<f;v+=n)(x=e[v])<r&&(r=x),(o=e[v+1])<a&&(a=o),i<x&&(i=x),u<o&&(u=o);s=Math.max(i-r,u-a)}return d(h,l,n,r,a,s),l}function y(e,t,n,r,a){var i,u;if(a===0<A(e,t,n,r))for(i=t;i<n;i+=r)u=o(i,e[i],e[i+1],u);else for(i=n-r;t<=i;i-=r)u=o(i,e[i],e[i+1],u);return u&&x(u,u.next)&&(S(u),u=u.next),u}function c(e,t){if(!e)return e;t=t||e;var n,r=e;do{if(n=!1,r.steiner||!x(r,r.next)&&0!==w(r.prev,r,r.next))r=r.next;else{if(S(r),(r=t=r.prev)===r.next)return null;n=!0}}while(n||r!==t);return t}function d(e,t,n,r,a,i,u){if(e){!u&&i&&function(e,t,n,r){var a=e;for(;null===a.z&&(a.z=v(a.x,a.y,t,n,r)),a.prevZ=a.prev,a.nextZ=a.next,a=a.next,a!==e;);a.prevZ.nextZ=null,a.prevZ=null,function(e){var t,n,r,a,i,u,x,o,s=1;do{for(n=e,i=e=null,u=0;n;){for(u++,r=n,t=x=0;t<s&&(x++,r=r.nextZ);t++);for(o=s;0<x||0<o&&r;)0===x?(r=(a=r).nextZ,o--):0!==o&&r?n.z<=r.z?(n=(a=n).nextZ,x--):(r=(a=r).nextZ,o--):(n=(a=n).nextZ,x--),i?i.nextZ=a:e=a,a.prevZ=i,i=a;n=r}i.nextZ=null,s*=2}while(1<u)}(a)}(e,r,a,i);for(var x,o,s=e;e.prev!==e.next;)if(x=e.prev,o=e.next,i?f(e,r,a,i):p(e))t.push(x.i/n),t.push(e.i/n),t.push(o.i/n),S(e),e=o.next,s=o.next;else if((e=o)===s){u?1===u?d(e=h(e,t,n),t,n,r,a,i,2):2===u&&l(e,t,n,r,a,i):d(c(e),t,n,r,a,i,1);break}}}function p(e){var t=e.prev,n=e,r=e.next;if(0<=w(t,n,r))return!1;for(var a=e.next.next;a!==e.prev;){if(b(t.x,t.y,n.x,n.y,r.x,r.y,a.x,a.y)&&0<=w(a.prev,a,a.next))return!1;a=a.next}return!0}function f(e,t,n,r){var a=e.prev,i=e,u=e.next;if(0<=w(a,i,u))return!1;for(var x=a.x<i.x?a.x<u.x?a.x:u.x:i.x<u.x?i.x:u.x,o=a.y<i.y?a.y<u.y?a.y:u.y:i.y<u.y?i.y:u.y,s=a.x>i.x?a.x>u.x?a.x:u.x:i.x>u.x?i.x:u.x,p=a.y>i.y?a.y>u.y?a.y:u.y:i.y>u.y?i.y:u.y,f=v(x,o,t,n,r),h=v(s,p,t,n,r),l=e.nextZ;l&&l.z<=h;){if(l!==e.prev&&l!==e.next&&b(a.x,a.y,i.x,i.y,u.x,u.y,l.x,l.y)&&0<=w(l.prev,l,l.next))return!1;l=l.nextZ}for(l=e.prevZ;l&&l.z>=f;){if(l!==e.prev&&l!==e.next&&b(a.x,a.y,i.x,i.y,u.x,u.y,l.x,l.y)&&0<=w(l.prev,l,l.next))return!1;l=l.prevZ}return!0}function h(e,t,n){var r=e;do{var a=r.prev,i=r.next.next;!x(a,i)&&E(a,r,r.next,i)&&Z(a,i)&&Z(i,a)&&(t.push(a.i/n),t.push(r.i/n),t.push(i.i/n),S(r),S(r.next),r=e=i),r=r.next}while(r!==e);return r}function l(e,t,n,r,a,i){var u,x,o=e;do{for(var s=o.next.next;s!==o.prev;){if(o.i!==s.i&&(x=s,(u=o).next.i!==x.i&&u.prev.i!==x.i&&!function(e,t){var n=e;do{if(n.i!==e.i&&n.next.i!==e.i&&n.i!==t.i&&n.next.i!==t.i&&E(n,n.next,e,t))return!0;n=n.next}while(n!==e);return!1}(u,x)&&Z(u,x)&&Z(x,u)&&function(e,t){var n=e,r=!1,a=(e.x+t.x)/2,i=(e.y+t.y)/2;for(;n.y>i!=n.next.y>i&&a<(n.next.x-n.x)*(i-n.y)/(n.next.y-n.y)+n.x&&(r=!r),n=n.next,n!==e;);return r}(u,x))){var p=M(o,s);return o=c(o,o.next),p=c(p,p.next),d(o,t,n,r,a,i),void d(p,t,n,r,a,i)}s=s.next}o=o.next}while(o!==e)}function C(e,t){return e.x-t.x}function m(e,t){if(t=function(e,t){var n,r=t,a=e.x,i=e.y,u=-1/0;do{if(i<=r.y&&i>=r.next.y){var x=r.x+(i-r.y)*(r.next.x-r.x)/(r.next.y-r.y);if(x<=a&&u<x){if((u=x)===a){if(i===r.y)return r;if(i===r.next.y)return r.next}n=r.x<r.next.x?r:r.next}}r=r.next}while(r!==t);if(!n)return null;if(a===u)return n.prev;var o,s=n,p=n.x,f=n.y,h=1/0;r=n.next;for(;r!==s;)a>=r.x&&r.x>=p&&b(i<f?a:u,i,p,f,i<f?u:a,i,r.x,r.y)&&((o=Math.abs(i-r.y)/(a-r.x))<h||o===h&&r.x>n.x)&&Z(r,e)&&(n=r,h=o),r=r.next;return n}(e,t)){var n=M(t,e);c(n,n.next)}}function v(e,t,n,r,a){return(e=1431655765&((e=858993459&((e=252645135&((e=16711935&((e=32767*(e-n)/a)|e<<8))|e<<4))|e<<2))|e<<1))|(t=1431655765&((t=858993459&((t=252645135&((t=16711935&((t=32767*(t-r)/a)|t<<8))|t<<4))|t<<2))|t<<1))<<1}function g(e){for(var t=e,n=e;t.x<n.x&&(n=t),(t=t.next)!==e;);return n}function b(e,t,n,r,a,i,u,x){return 0<=(a-u)*(t-x)-(e-u)*(i-x)&&0<=(e-u)*(r-x)-(n-u)*(t-x)&&0<=(n-u)*(i-x)-(a-u)*(r-x)}function w(e,t,n){return(t.y-e.y)*(n.x-t.x)-(t.x-e.x)*(n.y-t.y)}function x(e,t){return e.x===t.x&&e.y===t.y}function E(e,t,n,r){return!!(x(e,t)&&x(n,r)||x(e,r)&&x(n,t))||0<w(e,t,n)!=0<w(e,t,r)&&0<w(n,r,e)!=0<w(n,r,t)}function Z(e,t){return w(e.prev,e,e.next)<0?0<=w(e,t,e.next)&&0<=w(e,e.prev,t):w(e,t,e.prev)<0||w(e,e.next,t)<0}function M(e,t){var n=new u(e.i,e.x,e.y),r=new u(t.i,t.x,t.y),a=e.next,i=t.prev;return(e.next=t).prev=e,(n.next=a).prev=n,(r.next=n).prev=r,(i.next=r).prev=i,r}function o(e,t,n,r){var a=new u(e,t,n);return r?(a.next=r.next,(a.prev=r).next.prev=a,r.next=a):(a.prev=a).next=a,a}function S(e){e.next.prev=e.prev,e.prev.next=e.next,e.prevZ&&(e.prevZ.nextZ=e.nextZ),e.nextZ&&(e.nextZ.prevZ=e.prevZ)}function u(e,t,n){this.i=e,this.x=t,this.y=n,this.prev=null,this.next=null,this.z=null,this.prevZ=null,this.nextZ=null,this.steiner=!1}function A(e,t,n,r){for(var a=0,i=t,u=n-r;i<n;i+=r)a+=(e[u]-e[i])*(e[i+1]+e[u+1]),u=i;return a}a.deviation=function(e,t,n,r){var a=t&&t.length,i=a?t[0]*n:e.length,u=Math.abs(A(e,0,i,n));if(a)for(var x=0,o=t.length;x<o;x++){var s=t[x]*n,p=x<o-1?t[x+1]*n:e.length;u-=Math.abs(A(e,s,p,n))}var f=0;for(x=0;x<r.length;x+=3){var h=r[x]*n,l=r[x+1]*n,v=r[x+2]*n;f+=Math.abs((e[h]-e[v])*(e[1+l]-e[1+h])-(e[h]-e[l])*(e[1+v]-e[1+h]))}return 0===u&&0===f?0:Math.abs((f-u)/u)},a.flatten=function(e){for(var t=e[0][0].length,n={vertices:[],holes:[],dimensions:t},r=0,a=0;a<e.length;a++){for(var i=0;i<e[a].length;i++)for(var u=0;u<t;u++)n.vertices.push(e[a][i][u]);0<a&&(r+=e[a-1].length,n.holes.push(r))}return n};var i={CLOCKWISE:r.WebGLConstants.CW,COUNTER_CLOCKWISE:r.WebGLConstants.CCW,validate:function(e){return e===i.CLOCKWISE||e===i.COUNTER_CLOCKWISE}},s=n.freezeObject(i),z=new I.Cartesian3,R=new I.Cartesian3,L={computeArea2D:function(e){for(var t=e.length,n=0,r=t-1,a=0;a<t;r=a++){var i=e[r],u=e[a];n+=i.x*u.y-u.x*i.y}return.5*n},computeWindingOrder2D:function(e){return 0<L.computeArea2D(e)?s.COUNTER_CLOCKWISE:s.CLOCKWISE},triangulate:function(e,t){return a(I.Cartesian2.packArray(e),t,2)}},_=new I.Cartesian3,K=new I.Cartesian3,V=new I.Cartesian3,D=new I.Cartesian3,G=new I.Cartesian3,O=new I.Cartesian3,k=new I.Cartesian3;L.computeSubdivision=function(e,t,n,r){r=W.defaultValue(r,P.CesiumMath.RADIANS_PER_DEGREE);var a,i=n.slice(0),u=t.length,x=new Array(3*u),o=0;for(a=0;a<u;a++){var s=t[a];x[o++]=s.x,x[o++]=s.y,x[o++]=s.z}for(var p=[],f={},h=e.maximumRadius,l=P.CesiumMath.chordLength(r,h),v=l*l;0<i.length;){var y,c,d=i.pop(),C=i.pop(),m=i.pop(),g=I.Cartesian3.fromArray(x,3*m,_),b=I.Cartesian3.fromArray(x,3*C,K),w=I.Cartesian3.fromArray(x,3*d,V),E=I.Cartesian3.multiplyByScalar(I.Cartesian3.normalize(g,D),h,D),Z=I.Cartesian3.multiplyByScalar(I.Cartesian3.normalize(b,G),h,G),M=I.Cartesian3.multiplyByScalar(I.Cartesian3.normalize(w,O),h,O),S=I.Cartesian3.magnitudeSquared(I.Cartesian3.subtract(E,Z,k)),A=I.Cartesian3.magnitudeSquared(I.Cartesian3.subtract(Z,M,k)),z=I.Cartesian3.magnitudeSquared(I.Cartesian3.subtract(M,E,k)),R=Math.max(S,A,z);v<R?S===R?(a=f[y=Math.min(m,C)+" "+Math.max(m,C)],T.defined(a)||(c=I.Cartesian3.add(g,b,k),I.Cartesian3.multiplyByScalar(c,.5,c),x.push(c.x,c.y,c.z),a=x.length/3-1,f[y]=a),i.push(m,a,d),i.push(a,C,d)):A===R?(a=f[y=Math.min(C,d)+" "+Math.max(C,d)],T.defined(a)||(c=I.Cartesian3.add(b,w,k),I.Cartesian3.multiplyByScalar(c,.5,c),x.push(c.x,c.y,c.z),a=x.length/3-1,f[y]=a),i.push(C,a,m),i.push(a,d,m)):z===R&&(a=f[y=Math.min(d,m)+" "+Math.max(d,m)],T.defined(a)||(c=I.Cartesian3.add(w,g,k),I.Cartesian3.multiplyByScalar(c,.5,c),x.push(c.x,c.y,c.z),a=x.length/3-1,f[y]=a),i.push(d,a,C),i.push(a,m,C)):(p.push(m),p.push(C),p.push(d))}return new N.Geometry({attributes:{position:new N.GeometryAttribute({componentDatatype:B.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:x})},indices:p,primitiveType:N.PrimitiveType.TRIANGLES})};var q=new I.Cartographic,F=new I.Cartographic,j=new I.Cartographic,H=new I.Cartographic;L.computeRhumbLineSubdivision=function(e,t,n,r){r=W.defaultValue(r,P.CesiumMath.RADIANS_PER_DEGREE);var a,i=n.slice(0),u=t.length,x=new Array(3*u),o=0;for(a=0;a<u;a++){var s=t[a];x[o++]=s.x,x[o++]=s.y,x[o++]=s.z}for(var p=[],f={},h=e.maximumRadius,l=P.CesiumMath.chordLength(r,h),v=new U.EllipsoidRhumbLine(void 0,void 0,e),y=new U.EllipsoidRhumbLine(void 0,void 0,e),c=new U.EllipsoidRhumbLine(void 0,void 0,e);0<i.length;){var d=i.pop(),C=i.pop(),m=i.pop(),g=I.Cartesian3.fromArray(x,3*m,_),b=I.Cartesian3.fromArray(x,3*C,K),w=I.Cartesian3.fromArray(x,3*d,V),E=e.cartesianToCartographic(g,q),Z=e.cartesianToCartographic(b,F),M=e.cartesianToCartographic(w,j);v.setEndPoints(E,Z);var S=v.surfaceDistance;y.setEndPoints(Z,M);var A=y.surfaceDistance;c.setEndPoints(M,E);var z,R,L,D,G=c.surfaceDistance,O=Math.max(S,A,G);l<O?S===O?(a=f[z=Math.min(m,C)+" "+Math.max(m,C)],T.defined(a)||(R=v.interpolateUsingFraction(.5,H),L=.5*(E.height+Z.height),D=I.Cartesian3.fromRadians(R.longitude,R.latitude,L,e,k),x.push(D.x,D.y,D.z),a=x.length/3-1,f[z]=a),i.push(m,a,d),i.push(a,C,d)):A===O?(a=f[z=Math.min(C,d)+" "+Math.max(C,d)],T.defined(a)||(R=y.interpolateUsingFraction(.5,H),L=.5*(Z.height+M.height),D=I.Cartesian3.fromRadians(R.longitude,R.latitude,L,e,k),x.push(D.x,D.y,D.z),a=x.length/3-1,f[z]=a),i.push(C,a,m),i.push(a,d,m)):G===O&&(a=f[z=Math.min(d,m)+" "+Math.max(d,m)],T.defined(a)||(R=c.interpolateUsingFraction(.5,H),L=.5*(M.height+E.height),D=I.Cartesian3.fromRadians(R.longitude,R.latitude,L,e,k),x.push(D.x,D.y,D.z),a=x.length/3-1,f[z]=a),i.push(d,a,C),i.push(a,m,C)):(p.push(m),p.push(C),p.push(d))}return new N.Geometry({attributes:{position:new N.GeometryAttribute({componentDatatype:B.ComponentDatatype.DOUBLE,componentsPerAttribute:3,values:x})},indices:p,primitiveType:N.PrimitiveType.TRIANGLES})},L.scaleToGeodeticHeight=function(e,t,n,r){n=W.defaultValue(n,I.Ellipsoid.WGS84);var a=z,i=R;if(t=W.defaultValue(t,0),r=W.defaultValue(r,!0),T.defined(e))for(var u=e.length,x=0;x<u;x+=3)I.Cartesian3.fromArray(e,x,i),r&&(i=n.scaleToGeodeticSurface(i,i)),0!==t&&(a=n.geodeticSurfaceNormal(i,a),I.Cartesian3.multiplyByScalar(a,t,a),I.Cartesian3.add(i,a,i)),e[x]=i.x,e[x+1]=i.y,e[x+2]=i.z;return e},e.PolygonPipeline=L,e.WindingOrder=s});
