'use strict';

var geoTrans = {};

/**
 * Ellipsoid parameters; major axis (a), minor axis (b), and flattening (f) for each ellipsoid.
 */
geoTrans.ellipsoid = {
    WGS84:         { a: 6378137,     b: 6356752.314245, f: 1/298.257223563 },
    Airy1830:      { a: 6377563.396, b: 6356256.909,    f: 1/299.3249646   },
    AiryModified:  { a: 6377340.189, b: 6356034.448,    f: 1/299.3249646   },
    Bessel1841:    { a: 6377397.155, b: 6356078.962818, f: 1/299.1528128   },
    Clarke1866:    { a: 6378206.4,   b: 6356583.8,      f: 1/294.978698214 },
    Clarke1880IGN: { a: 6378249.2,   b: 6356515.0,      f: 1/293.466021294 },
    GRS80:         { a: 6378137,     b: 6356752.314140, f: 1/298.257222101 },
    Intl1924:      { a: 6378388,     b: 6356911.946,    f: 1/297           }, // aka Hayford
    WGS72:         { a: 6378135,     b: 6356750.5,      f: 1/298.26        }
};

/**
 * Datums; with associated ellipsoid, and Helmert transform parameters to convert from WGS 84 into
 * given datum.
 *
 * Note that precision of various datums will vary, and WGS-84 (original) is not defined to be
 * accurate to better than ±1 metre. No transformation should be assumed to be accurate to better
 * than a meter; for many datums somewhat less.
 */
geoTrans.datum = {
    // transforms: t in metres, s in ppm, r in arcseconds                    tx       ty        tz       s        rx       ry       rz
    ED50:       { ellipsoid: geoTrans.ellipsoid.Intl1924,      transform: [   89.5,    93.8,    123.1,    -1.2,     0.0,     0.0,     0.156  ] },
    Irl1975:    { ellipsoid: geoTrans.ellipsoid.AiryModified,  transform: [ -482.530, 130.596, -564.557,  -8.150,  -1.042,  -0.214,  -0.631  ] },
    NAD27:      { ellipsoid: geoTrans.ellipsoid.Clarke1866,    transform: [    8,    -160,     -176,       0,       0,       0,       0      ] },
    NAD83:      { ellipsoid: geoTrans.ellipsoid.GRS80,         transform: [    1.004,  -1.910,   -0.515,  -0.0015,  0.0267,  0.00034, 0.011  ] },
    NTF:        { ellipsoid: geoTrans.ellipsoid.Clarke1880IGN, transform: [  168,      60,     -320,       0,       0,       0,       0      ] },
    OSGB36:     { ellipsoid: geoTrans.ellipsoid.Airy1830,      transform: [ -446.448, 125.157, -542.060,  20.4894, -0.1502, -0.2470, -0.8421 ] },
    Potsdam:    { ellipsoid: geoTrans.ellipsoid.Bessel1841,    transform: [ -582,    -105,     -414,      -8.3,     1.04,    0.35,   -3.08   ] },
    TokyoJapan: { ellipsoid: geoTrans.ellipsoid.Bessel1841,    transform: [  148,    -507,     -685,       0,       0,       0,       0      ] },
    WGS72:      { ellipsoid: geoTrans.ellipsoid.WGS72,         transform: [    0,       0,     -4.5,      -0.22,    0,       0,       0.554  ] },
    WGS84:      { ellipsoid: geoTrans.ellipsoid.WGS84,         transform: [    0.0,     0.0,      0.0,     0.0,     0.0,     0.0,     0.0    ] }
};
/* sources:
 * - ED50:          www.gov.uk/guidance/oil-and-gas-petroleum-operations-notices#pon-4
 * - Irl1975:       www.osi.ie/wp-content/uploads/2015/05/transformations_booklet.pdf
 *   ... note: many sources have opposite sign to rotations - to be checked!
 * - NAD27:         en.wikipedia.org/wiki/Helmert_transformation
 * - NAD83: (2009); www.uvm.edu/giv/resources/WGS84_NAD83.pdf
 *   ... note: functionally ≡ WGS84 - if you *really* need to convert WGS84<->NAD83, you need more knowledge than this!
 * - NTF:           Nouvelle Triangulation Francaise geodesie.ign.fr/contenu/fichiers/Changement_systeme_geodesique.pdf
 * - OSGB36:        www.ordnancesurvey.co.uk/docs/support/guide-coordinate-systems-great-britain.pdf
 * - Potsdam:       kartoweb.itc.nl/geometrics/Coordinate%20transformations/coordtrans.html
 * - TokyoJapan:    www.geocachingtoolbox.com?page=datumEllipsoidDetails
 * - WGS72:         www.icao.int/safety/pbn/documentation/eurocontrol/eurocontrol wgs 84 implementation manual.pdf
 *
 * more transform parameters are available from earth-info.nga.mil/GandG/coordsys/datums/NATO_DT.pdf,
 * www.fieldenmaps.info/cconv/web/cconv_params.js
 */

/*
 * Latitude bands C..X 8° each, covering 80°S to 84°N
 */
geoTrans.latBands = 'CDEFGHJKLMNPQRSTUVWXX';    // X is repeated for 80-84°N

/*
 * 100km grid square column (‘e’) letters repeat every third zone
 */
geoTrans.e100kLetters = ['ABCDEFGH', 'JKLMNPQR', 'STUVWXYZ'];

/*
 * 100km grid square row (‘n’) letters repeat every other zone
 */
geoTrans.n100kLetters = [ 'ABCDEFGHJKLMNPQRSTUV', 'FGHJKLMNPQRSTUVABCDE' ];

/**
 * (24 * 12) Band
 */
geoTrans.latDigits1 = ['G', 'H', 'J', 'K', 'L', 'M'];
geoTrans.latDigits2 = ['F', 'E', 'D', 'C', 'B', 'A'];
geoTrans.lonDigits1 = ['N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
geoTrans.lonDigits2 = ['M', 'L', 'K', 'J', 'H', 'G', 'F', 'E', 'D', 'C', 'B', 'A'];

/**
 * 15Digits
 */
geoTrans.latlonDigit = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
    'J', 'K', 'L', 'M', 'N', 'P', 'Q'];

/**
 * gars
 */
geoTrans.letterArray = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L',
    'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

geoTrans.isDigit = function isDigit(code) {
    return code > 47 && code < 58;
};

/**
 * char 체크
 * @param c
 * @returns {boolean}
 */
geoTrans.isLetter = function isLetter(c) {
    var code = c.charCodeAt(0);
    return !(!(code >= 65 && code <= 90) || (code >= 97 && code <= 122));
}

/**
 * Functions for parsing and representing degrees / minutes / seconds.
 * @type {{}}
 */
geoTrans.Dms = {};
geoTrans.Dms.separator = '';

/**
 * Parses string representing degrees/minutes/seconds into numeric degrees.
 *
 * This is very flexible on formats, allowing signed decimal degrees, or deg-min-sec optionally
 * suffixed by compass direction (NSEW). A variety of separators are accepted (eg 3° 37′ 09″W).
 * Seconds and minutes may be omitted.
 *
 * @param   {string|number} dmsStr - Degrees or deg/min/sec in variety of formats.
 * @returns {number} Degrees as decimal number.
 *
 * @example
 *     var lat = Dms.parseDMS('51° 28′ 40.12″ N');
 *     var lon = Dms.parseDMS('000° 00′ 05.31″ W');
 *     var p1 = new LatLon(lat, lon); // 51.4778°N, 000.0015°W
 */
geoTrans.Dms.parseDMS = function parseDMS(dmsStr) {
    // check for signed decimal degrees without NSEW, if so return it directly
    if (typeof dmsStr === 'number' && isFinite(dmsStr)) return Number(dmsStr);

    // strip off any sign or compass dir'n & split out separate d/m/s
    var dms = String(dmsStr).trim().replace(/^-/, '')
        .replace(/[NSEW]$/i, '').split(/[^0-9.,]+/);
    if (dms[dms.length-1] === '') dms.splice(dms.length-1);  // from trailing symbol

    if (dms.length === 0) return NaN;

    // and convert to decimal degrees...
    var deg;
    switch (dms.length) {
        case 3:  // interpret 3-part result as d/m/s
            deg = dms[0] / 1 + dms[1] / 60 + dms[2] / 3600;
            break;
        case 2:  // interpret 2-part result as d/m
            deg = dms[0] / 1 + dms[1] / 60;
            break;
        case 1:  // just d (possibly decimal) or non-separated dddmmss
            deg = dms[0];
            // check for fixed-width unseparated format eg 0033709W
            //if (/[NS]/i.test(dmsStr)) deg = '0' + deg;  // - normalise N/S to 3-digit degrees
            //if (/[0-9]{7}/.test(deg)) deg = deg.slice(0,3)/1 + deg.slice(3,5)/60 + deg.slice(5)/3600;
            break;
        default:
            return NaN;
    }
    if (/^-|[WS]$/i.test(dmsStr.trim())) deg = -deg; // take '-', west and south as -ve

    return Number(deg);
};

/**
 * Converts decimal degrees to deg/min/sec format
 *  - degree, prime, double-prime symbols are added, but sign is discarded, though no compass
 *    direction is added.
 *
 * @private
 * @param   {number} deg - Degrees to be formatted as specified.
 * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
 * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
 * @returns {string|null} Degrees formatted as deg/min/secs according to specified format.
 */
geoTrans.Dms.toDMS = function toDMS(deg, format, dp) {
    if (isNaN(deg)) return null;  // give up here if we can't make a number from deg

    // default values
    if (format === undefined) format = 'dms';
    if (dp === undefined) {
        switch (format) {
            case 'd':    case 'deg':         dp = 4; break;
            case 'dm':   case 'deg+min':     dp = 2; break;
            case 'dms':  case 'deg+min+sec': dp = 0; break;
            default:    format = 'dms'; dp = 0;  // be forgiving on invalid format
        }
    }

    deg = Math.abs(deg);  // (unsigned result ready for appending compass dir'n)

    var dms, d, m, s;
    switch (format) {
        default: // invalid format spec!
        case 'd': case 'deg':
            d = deg.toFixed(dp);                       // round/right-pad degrees
            if (d < 100) d = '0' + d;                    // left-pad with leading zeros (note may include decimals)
            if (d < 10) d = '0' + d;
            dms = d + '°';
            break;
        case 'dm': case 'deg+min':
            d = Math.floor(deg);                       // get component deg
            m = Number(((deg * 60) % 60).toFixed(dp));           // get component min & round/right-pad
            if (m === 60) { m = 0; d++; }               // check for rounding up
            d = ('000' + d).slice(-3);                   // left-pad with leading zeros
            if (m < 10) m = '0' + m;                     // left-pad with leading zeros (note may include decimals)
            dms = d + '°' + this.separator + m + '′';
            break;
        case 'dms': case 'deg+min+sec':
            d = Math.floor(deg);                       // get component deg
            m = Math.floor((deg * 3600) / 60) % 60;        // get component min
            s = Number((deg * 3600 % 60).toFixed(dp));           // get component sec & round/right-pad
            if (s === 60) { s = (0).toFixed(dp); m++; } // check for rounding up
            if (m === 60) { m = 0; d++; }               // check for rounding up
            d = ('000' + d).slice(-3);                   // left-pad with leading zeros
            m = ('00' + m).slice(-2);                    // left-pad with leading zeros
            if (s < 10) s = '0' + s;                     // left-pad with leading zeros (note may include decimals)
            dms = d + '°' + this.separator + m + '′' + this.separator + s + '″';
            break;
    }

    return dms;
};

/**
 * Converts numeric degrees to deg/min/sec latitude (2-digit degrees, suffixed with N/S).
 *
 * @param   {number} deg - Degrees to be formatted as specified.
 * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
 * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
 * @returns {string|null} Degrees formatted as deg/min/secs according to specified format.
 */
geoTrans.Dms.toLat = function toLat(deg, format, dp) {
    var lat = this.toDMS(deg, format, dp);
    return lat === null ? '–' : lat.slice(1) + this.separator + (deg < 0 ? 'S' : 'N');  // knock off initial '0' for lat!
};

/**
 * Convert numeric degrees to deg/min/sec longitude (3-digit degrees, suffixed with E/W)
 *
 * @param   {number} deg - Degrees to be formatted as specified.
 * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
 * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
 * @returns {string|null} Degrees formatted as deg/min/secs according to specified format.
 */
geoTrans.Dms.toLon = function toLon(deg, format, dp) {
    var lon = this.toDMS(deg, format, dp);
    return lon === null ? '–' : lon + this.separator + (deg < 0 ? 'W' : 'E');
};

/**
 * Converts numeric degrees to deg/min/sec as a bearing (0°..360°)
 *
 * @param   {number} deg - Degrees to be formatted as specified.
 * @param   {string} [format=dms] - Return value as 'd', 'dm', 'dms' for deg, deg+min, deg+min+sec.
 * @param   {number} [dp=0|2|4] - Number of decimal places to use – default 0 for dms, 2 for dm, 4 for d.
 * @returns {string|null} Degrees formatted as deg/min/secs according to specified format.
 */
geoTrans.Dms.toBrng = function toBrng(deg, format, dp) {
    deg = (Number(deg) + 360) % 360;  // normalise -ve values to 180°..360°
    var brng =  this.toDMS(deg, format, dp);
    return brng === null ? '–' : brng.replace('360', '0');  // just in case rounding took us up to 360°!
};

/**
 * Returns compass point (to given precision) for supplied bearing.
 *
 * @param   {number} bearing - Bearing in degrees from north.
 * @param   {number} [precision=3] - Precision (1:cardinal / 2:intercardinal / 3:secondary-intercardinal).
 * @returns {string} Compass point for supplied bearing.
 *
 * @example
 *   var point = Dms.compassPoint(24);    // point = 'NNE'
 *   var point = Dms.compassPoint(24, 1); // point = 'N'
 */
geoTrans.Dms.compassPoint = function compassPoint(bearing, precision) {
    if (precision === undefined) precision = 3;
    // note precision could be extended to 4 for quarter-winds (eg NbNW), but I think they are little used

    bearing = ((bearing % 360) + 360) % 360; // normalise to range 0..360°

    var cardinals = [
        'N', 'NNE', 'NE', 'ENE',
        'E', 'ESE', 'SE', 'SSE',
        'S', 'SSW', 'SW', 'WSW',
        'W', 'WNW', 'NW', 'NNW' ];
    var n = 4 * Math.pow(2, precision-1); // no of compass points at req’d precision (1=>4, 2=>8, 3=>16)
    return cardinals[Math.round(bearing * n / 360) % n * 16 / n];
};

/**
 * Creates a 3-d vector.
 *
 * The vector may be normalised, or use x/y/z values for eg height relative to the sphere or
 * ellipsoid, distance from earth centre, etc.
 *
 * @constructor
 * @param {number} x - X component of vector.
 * @param {number} y - Y component of vector.
 * @param {number} z - Z component of vector.
 */
geoTrans.Vector3d = function Vector3d(x, y, z) {
    // allow instantiation without 'new'
    if (!(this instanceof geoTrans.Vector3d)) return new geoTrans.Vector3d(x, y, z);

    this.x = Number(x);
    this.y = Number(y);
    this.z = Number(z);
};

/**
 * Adds supplied vector to ‘this’ vector.
 *
 * @param   {Vector3d} v - Vector to be added to this vector.
 * @returns {geoTrans.Vector3d} Vector representing sum of this and v.
 */
geoTrans.Vector3d.prototype.plus = function plus(v) {
    if (!(v instanceof geoTrans.Vector3d)) throw new TypeError('v is not Vector3d object');

    return new geoTrans.Vector3d(this.x + v.x, this.y + v.y, this.z + v.z);
};

/**
 * Subtracts supplied vector from ‘this’ vector.
 *
 * @param   {Vector3d} v - Vector to be subtracted from this vector.
 * @returns {geoTrans.Vector3d} Vector representing difference between this and v.
 */
geoTrans.Vector3d.prototype.minus = function minus(v) {
    if (!(v instanceof geoTrans.Vector3d)) throw new TypeError('v is not Vector3d object');

    return new geoTrans.Vector3d(this.x - v.x, this.y - v.y, this.z - v.z);
};

/**
 * Multiplies ‘this’ vector by a scalar value.
 *
 * @param   {number}   x - Factor to multiply this vector by.
 * @returns {geoTrans.Vector3d} Vector scaled by x.
 */
geoTrans.Vector3d.prototype.times = function times(x) {
    x = Number(x);

    return new geoTrans.Vector3d(this.x * x, this.y * x, this.z * x);
};

/**
 * Divides ‘this’ vector by a scalar value.
 *
 * @param   {number}   x - Factor to divide this vector by.
 * @returns {geoTrans.Vector3d} Vector divided by x.
 */
geoTrans.Vector3d.prototype.dividedBy = function dividedBy(x) {
    x = Number(x);

    return new geoTrans.Vector3d(this.x / x, this.y / x, this.z / x);
};

/**
 * Multiplies ‘this’ vector by the supplied vector using dot (scalar) product.
 *
 * @param   {Vector3d} v - Vector to be dotted with this vector.
 * @returns {number} Dot product of ‘this’ and v.
 */
geoTrans.Vector3d.prototype.dot = function dot(v) {
    if (!(v instanceof geoTrans.Vector3d)) throw new TypeError('v is not Vector3d object');

    return this.x * v.x + this.y * v.y + this.z * v.z;
};

/**
 * Multiplies ‘this’ vector by the supplied vector using cross (vector) product.
 *
 * @param   {Vector3d} v - Vector to be crossed with this vector.
 * @returns {geoTrans.Vector3d} Cross product of ‘this’ and v.
 */
geoTrans.Vector3d.prototype.cross = function cross(v) {
    if (!(v instanceof this.Vector3d)) throw new TypeError('v is not Vector3d object');

    var x = this.y * v.z - this.z * v.y;
    var y = this.z * v.x - this.x * v.z;
    var z = this.x * v.y - this.y * v.x;

    return new geoTrans.Vector3d(x, y, z);
};

/**
 * Negates a vector to point in the opposite direction
 *
 * @returns {geoTrans.Vector3d} Negated vector.
 */
geoTrans.Vector3d.prototype.negate = function negate() {
    return new geoTrans.Vector3d(-this.x, -this.y, -this.z);
};

/**
 * Length (magnitude or norm) of ‘this’ vector
 *
 * @returns {number} Magnitude of this vector.
 */
geoTrans.Vector3d.prototype.length = function length() {
    return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
};

/**
 * Normalizes a vector to its unit vector
 * – if the vector is already unit or is zero magnitude, this is a no-op.
 *
 * @returns {geoTrans.Vector3d} Normalised version of this vector.
 */
geoTrans.Vector3d.prototype.unit = function unit() {
    var norm = this.length();
    if (norm === 1) return this;
    if (norm === 0) return this;

    var x = this.x / norm;
    var y = this.y / norm;
    var z = this.z / norm;

    return new geoTrans.Vector3d(x, y, z);
};

/**
 * Calculates the angle between ‘this’ vector and supplied vector.
 *
 * @param   {Vector3d} v
 * @param   {Vector3d} [n] - Plane normal: if supplied, angle is -π..+π, signed +ve if this->v is
 *     clockwise looking along n, -ve in opposite direction (if not supplied, angle is always 0..π).
 * @returns {number} Angle (in radians) between this vector and supplied vector.
 */
geoTrans.Vector3d.prototype.angleTo = function angleTo(v, n) {
    if (!(v instanceof geoTrans.Vector3d)) throw new TypeError('v is not Vector3d object');
    if (!(n instanceof geoTrans.Vector3d || n === undefined)) throw new TypeError('n is not Vector3d object');

    var sign = n === undefined ? 1 : Math.sign(this.cross(v).dot(n));
    var sinθ = this.cross(v).length() * sign;
    var cosθ = this.dot(v);

    return Math.atan2(sinθ, cosθ);
};

/**
 * Rotates ‘this’ point around an axis by a specified angle.
 *
 * @param   {Vector3d} axis - The axis being rotated around.
 * @param   {number}   theta - The angle of rotation (in radians).
 * @returns {geoTrans.Vector3d} The rotated point.
 */
geoTrans.Vector3d.prototype.rotateAround = function rotateAround(axis, theta) {
    if (!(axis instanceof geoTrans.Vector3d)) throw new TypeError('axis is not Vector3d object');

    // en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
    // en.wikipedia.org/wiki/Quaternions_and_spatial_rotation#Quaternion-derived_rotation_matrix
    var p1 = this.unit();
    var p = [ p1.x, p1.y, p1.z ]; // the point being rotated
    var a = axis.unit();          // the axis being rotated around
    var s = Math.sin(theta);
    var c = Math.cos(theta);
    // quaternion-derived rotation matrix
    var q = [
        [ a.x * a.x * (1 - c) + c,     a.x * a.y * (1 - c) - a.z * s, a.x * a.z * (1 - c) + a.y * s ],
        [ a.y * a.x * (1 - c) + a.z * s, a.y * a.y * (1 - c) + c,     a.y * a.z * (1 - c) - a.x * s ],
        [ a.z * a.x * (1 - c) - a.y * s, a.z * a.y * (1 - c) + a.x * s, a.z * a.z * (1 - c) + c     ]
    ];
    // multiply q × p
    var qp = [ 0, 0, 0 ];
    for (var i = 0; i < 3; i++) {
        for (var j = 0; j < 3; j++) {
            qp[i] += q[i][j] * p[j];
        }
    }
    return new geoTrans.Vector3d(qp[0], qp[1], qp[2]);
};

/**
 * Applies Helmert transform to ‘this’ point using transform parameters t.
 *
 * @private
 * @param   {number[]} t - Transform to apply to this point.
 * @returns {geoTrans.Vector3d} Transformed point.
 */
geoTrans.Vector3d.prototype.applyTransform = function applyTransform(t) {
    // this point
    var x1 = this.x, y1 = this.y, z1 = this.z;

    // transform parameters
    var tx = t[0];                    // x-shift
    var ty = t[1];                    // y-shift
    var tz = t[2];                    // z-shift
    var s1 = t[3] / 1e6 + 1;            // scale: normalise parts-per-million to (s+1)
    var rx = (t[4] / 3600).toRadians(); // x-rotation: normalise arcseconds to radians
    var ry = (t[5] / 3600).toRadians(); // y-rotation: normalise arcseconds to radians
    var rz = (t[6] / 3600).toRadians(); // z-rotation: normalise arcseconds to radians

    // apply transform
    var x2 = tx + x1 * s1 - y1 * rz + z1 * ry;
    var y2 = ty + x1 * rz + y1 * s1 - z1 * rx;
    var z2 = tz - x1 * ry + y1 * rx + z1 * s1;

    return new geoTrans.Vector3d(x2, y2, z2);
};

/**
 * Converts ‘this’ (geocentric) cartesian (x/y/z) point to (ellipsoidal geodetic) latitude/longitude
 * coordinates on specified datum.
 *
 * Uses Bowring’s (1985) formulation for μm precision in concise form.
 *
 * @param {geoTrans.datum} datum - Datum to use when converting point.
 */
geoTrans.Vector3d.prototype.toLatLonE = function(datum) {
    var x = this.x, y = this.y, z = this.z;
    var a = datum.ellipsoid.a, b = datum.ellipsoid.b, f = datum.ellipsoid.f;

    var e2 = 2 * f - f * f;   // 1st eccentricity squared ≡ (a²-b²)/a²
    var ε2 = e2 / (1 - e2); // 2nd eccentricity squared ≡ (a²-b²)/b²
    var p = Math.sqrt(x * x + y * y); // distance from minor axis
    var R = Math.sqrt(p * p + z * z); // polar radius

    // parametric latitude (Bowring eqn 17, replacing tanβ = z·a / p·b)
    var tanβ = (b * z) / (a * p) * (1 + ε2 * b / R);
    var sinβ = tanβ / Math.sqrt(1 + tanβ * tanβ);
    var cosβ = sinβ / tanβ;

    // geodetic latitude (Bowring eqn 18: tanφ = z+ε²bsin³β / p−e²cos³β)
    var φ = isNaN(cosβ) ? 0 : Math.atan2(z + ε2 * b * sinβ * sinβ * sinβ, p - e2 * a * cosβ * cosβ * cosβ);

    // longitude
    var λ = Math.atan2(y, x);

    // height above ellipsoid (Bowring eqn 7) [not currently used]
    var sinφ = Math.sin(φ), cosφ = Math.cos(φ);
    var ν = a / Math.sqrt(1 - e2 * sinφ * sinφ); // length of the normal terminated by the minor axis
    var h = p * cosφ + z * sinφ - (a * a / ν);

    return new geoTrans.LatLon(λ.toDegrees(), φ.toDegrees(), datum);
};

/**
 * String representation of vector.
 *
 * @param   {number} [precision=3] - Number of decimal places to be used.
 * @returns {string} Vector represented as [x,y,z].
 */
geoTrans.Vector3d.prototype.toString = function toString(precision) {
    var p = (precision === undefined) ? 3 : Number(precision);

    return '[' + this.x.toFixed(p) + ',' + this.y.toFixed(p) + ',' + this.z.toFixed(p) + ']';
};

/**
 * Creates lat/lon (polar) point with latitude & longitude values, on a specified datum.
 *
 * @constructor
 * @param {number|string}       lon - Longitude in degrees.
 * @param {number|string}       lat - Geodetic latitude in degrees.
 * @param {geoTrans.datum} [datum=WGS84] - Datum this point is defined within.
 *
 * @example
 *     var p1 = new LatLon(128, 37, LatLon.datum.WGS84);
 */
geoTrans.LatLon = function LatLon(lon, lat, datum) {
    // allow instantiation without 'new'
    if (!(this instanceof geoTrans.LatLon)) return new geoTrans.LatLon(lon, lat, datum);

    if (datum === undefined) datum = geoTrans.datum.WGS84;

    this.lat = Number(lat);
    this.lon = Number(lon);
    this.datum = datum;
};

/**
 * Converts ‘this’ lat/lon coordinate to new coordinate system.
 *
 * @param   {geoTrans.datum} toDatum - Datum this coordinate is to be converted to.
 * @returns {geoTrans.LatLon} This point converted to new datum.
 *
 * @example
 *     var pWGS84 = new LatLon(128, 37, LatLon.datum.WGS84);
 *     var pOSGB = pWGS84.convertDatum(LatLon.datum.OSGB36); // 51.4773°N, 000.0000°E
 */
geoTrans.LatLon.prototype.convertDatum = function convertDatum(toDatum) {
    var oldLatLon = this;
    var transform = null;

    if (oldLatLon.datum === this.datum.WGS84) {
        // converting from WGS 84
        transform = toDatum.transform;
    }
    if (toDatum === geoTrans.datum.WGS84) {
        // converting to WGS 84; use inverse transform (don't overwrite original!)
        transform = [];
        for (var p = 0; p < 7; p++) transform[p] = -oldLatLon.datum.transform[p];
    }
    if (transform === null) {
        // neither this.datum nor toDatum are WGS84: convert this to WGS84 first
        oldLatLon = this.convertDatum(geoTrans.datum.WGS84);
        transform = toDatum.transform;
    }

    var oldCartesian = oldLatLon.toCartesian();                // convert polar to cartesian...
    var newCartesian = oldCartesian.applyTransform(transform); // ...apply transform...

    return newCartesian.toLatLonE(toDatum);  // ...and convert cartesian to polar
};

/**
 * Converts ‘this’ point from (geodetic) latitude/longitude coordinates to (geocentric) cartesian
 * (x/y/z) coordinates.
 *
 * @returns {geoTrans.Vector3d} Vector pointing to lat/lon point, with x, y, z in metres from earth centre.
 */
geoTrans.LatLon.prototype.toCartesian = function toCartesian() {
    var φ = this.lat.toRadians(), λ = this.lon.toRadians();
    var h = 0; // height above ellipsoid - not currently used
    var a = this.datum.ellipsoid.a, f = this.datum.ellipsoid.f;

    var sinφ = Math.sin(φ), cosφ = Math.cos(φ);
    var sinλ = Math.sin(λ), cosλ = Math.cos(λ);

    var eSq = 2 * f - f * f;                      // 1st eccentricity squared ≡ (a²-b²)/a²
    var ν = a / Math.sqrt(1 - eSq * sinφ * sinφ); // radius of curvature in prime vertical

    var x = (ν + h) * cosφ * cosλ;
    var y = (ν + h) * cosφ * sinλ;
    var z = (ν * (1 - eSq) + h) * sinφ;

    return new geoTrans.Vector3d(x, y, z);
};

/**
 * Converts latitude/longitude to UTM coordinate.
 *
 * Implements Karney’s method, using Krüger series to order n^6, giving results accurate to 5nm for
 * distances up to 3900km from the central meridian.
 *
 * @returns {geoTrans.Utm}   UTM coordinate.
 * @throws  {Error} If point not valid, if point outside latitude range.
 *
 * @example
 *   var latlong = new LatLon(128, 37);
 *   var utmCoord = latlong.toUtm(); // utmCoord.toString(): '31 N 448252 5411933'
 */
geoTrans.LatLon.prototype.toUtm = function toUtm() {
    if (isNaN(this.lat) || isNaN(this.lon)) throw new Error('Invalid point');
    if (!(-80 <= this.lat && this.lat <= 84)) throw new Error('Outside UTM limits');

    var falseEasting = 500e3, falseNorthing = 10000e3;

    var zone = Math.floor((this.lon+180)/6) + 1; // longitudinal zone
    var λ0 = ((zone-1)*6 - 180 + 3).toRadians(); // longitude of central meridian

    // ---- handle Norway/Svalbard exceptions
    // grid zones are 8° tall; 0°N is offset 10 into latitude bands array
    var mgrsLatBands = 'CDEFGHJKLMNPQRSTUVWXX'; // X is repeated for 80-84°N
    var latBand = mgrsLatBands.charAt(Math.floor(this.lat / 8 + 10));
    // adjust zone & central meridian for Norway
    if (zone === 31 && latBand === 'V' && this.lon >= 3) { zone++; λ0 += (6).toRadians(); }
    // adjust zone & central meridian for Svalbard
    if (zone === 32 && latBand === 'X' && this.lon <  9) { zone--; λ0 -= (6).toRadians(); }
    if (zone === 32 && latBand === 'X' && this.lon >= 9) { zone++; λ0 += (6).toRadians(); }
    if (zone === 34 && latBand === 'X' && this.lon < 21) { zone--; λ0 -= (6).toRadians(); }
    if (zone === 34 && latBand === 'X' && this.lon >= 21) { zone++; λ0 += (6).toRadians(); }
    if (zone === 36 && latBand === 'X' && this.lon < 33) { zone--; λ0 -= (6).toRadians(); }
    if (zone === 36 && latBand === 'X' && this.lon >= 33) { zone++; λ0 += (6).toRadians(); }

    var φ = this.lat.toRadians();      // latitude ± from equator
    var λ = this.lon.toRadians() - λ0; // longitude ± from central meridian

    var a = this.datum.ellipsoid.a, f = this.datum.ellipsoid.f;
    // WGS 84: a = 6378137, b = 6356752.314245, f = 1/298.257223563;

    var k0 = 0.9996; // UTM scale on the central meridian

    // ---- easting, northing: Karney 2011 Eq 7-14, 29, 35:

    var e = Math.sqrt(f*(2-f)); // eccentricity
    var n = f / (2 - f);        // 3rd flattening
    var n2 = n*n, n3 = n*n2, n4 = n*n3, n5 = n*n4, n6 = n*n5; // TODO: compare Horner-form accuracy?

    var cosλ = Math.cos(λ), sinλ = Math.sin(λ), tanλ = Math.tan(λ);

    var τ = Math.tan(φ); // τ ≡ tanφ, τʹ ≡ tanφʹ; prime (ʹ) indicates angles on the conformal sphere
    var σ = Math.sinh(e*Math.atanh(e*τ/Math.sqrt(1+τ*τ)));

    var τʹ = τ*Math.sqrt(1+σ*σ) - σ*Math.sqrt(1+τ*τ);

    var ξʹ = Math.atan2(τʹ, cosλ);
    var ηʹ = Math.asinh(sinλ / Math.sqrt(τʹ*τʹ + cosλ*cosλ));

    var A = a/(1+n) * (1 + 1/4*n2 + 1/64*n4 + 1/256*n6); // 2πA is the circumference of a meridian

    var α = [ null, // note α is one-based array (6th order Krüger expressions)
        1/2*n - 2/3*n2 + 5/16*n3 +   41/180*n4 -     127/288*n5 +      7891/37800*n6,
        13/48*n2 -  3/5*n3 + 557/1440*n4 +     281/630*n5 - 1983433/1935360*n6,
        61/240*n3 -  103/140*n4 + 15061/26880*n5 +   167603/181440*n6,
        49561/161280*n4 -     179/168*n5 + 6601661/7257600*n6,
        34729/80640*n5 - 3418889/1995840*n6,
        212378941/319334400*n6 ];

    var ξ = ξʹ;
    for (var j=1; j<=6; j++) ξ += α[j] * Math.sin(2*j*ξʹ) * Math.cosh(2*j*ηʹ);

    var η = ηʹ;
    for (var j=1; j<=6; j++) η += α[j] * Math.cos(2*j*ξʹ) * Math.sinh(2*j*ηʹ);

    var x = k0 * A * η;
    var y = k0 * A * ξ;

    // ---- convergence: Karney 2011 Eq 23, 24

    var pʹ = 1;
    for (var j=1; j<=6; j++) pʹ += 2*j*α[j] * Math.cos(2*j*ξʹ) * Math.cosh(2*j*ηʹ);
    var qʹ = 0;
    for (var j=1; j<=6; j++) qʹ += 2*j*α[j] * Math.sin(2*j*ξʹ) * Math.sinh(2*j*ηʹ);

    var γʹ = Math.atan(τʹ / Math.sqrt(1+τʹ*τʹ)*tanλ);
    var γʺ = Math.atan2(qʹ, pʹ);

    var γ = γʹ + γʺ;

    // ---- scale: Karney 2011 Eq 25

    var sinφ = Math.sin(φ);
    var kʹ = Math.sqrt(1 - e*e*sinφ*sinφ) * Math.sqrt(1 + τ*τ) / Math.sqrt(τʹ*τʹ + cosλ*cosλ);
    var kʺ = A / a * Math.sqrt(pʹ*pʹ + qʹ*qʹ);

    var k = k0 * kʹ * kʺ;

    // ------------

    // shift x/y to false origins
    x = x + falseEasting;             // make x relative to false easting
    if (y < 0) y = y + falseNorthing; // make y in southern hemisphere relative to false northing

    // round to reasonable precision
    x = Number(x.toFixed(6)); // nm precision
    y = Number(y.toFixed(6)); // nm precision
    var convergence = Number(γ.toDegrees().toFixed(9));
    var scale = Number(k.toFixed(12));

    var h = this.lat >= 0 ? 'N' : 'S'; // hemisphere

    return new geoTrans.Utm(zone, h, x, y, this.datum, convergence, scale);
};

/**
 * Returns a string representation of ‘this’ point, formatted as degrees, degrees+minutes, or
 * degrees+minutes+seconds.
 *
 * @param   {string} [format=dms] - Format point as 'd', 'dm', 'dms'.
 * @param   {number} [dp=0|2|4] - Number of decimal places to use - default 0 for dms, 2 for dm, 4 for d.
 * @returns {string} Comma-separated longitude/latitude.
 */
geoTrans.LatLon.prototype.toString = function toString(format, dp) {
    return geoTrans.Dms.toLon(this.lon, format, dp) + ', ' + geoTrans.Dms.toLat(this.lat, format, dp);
};

/**
 * Creates a Utm coordinate object.
 *
 * @constructor
 * @param  {number} zone - UTM 6° longitudinal zone (1..60 covering 180°W..180°E).
 * @param  {string} hemisphere - N for northern hemisphere, S for southern hemisphere.
 * @param  {number} easting - Easting in metres from false easting (-500km from central meridian).
 * @param  {number} northing - Northing in metres from equator (N) or from false northing -10,000km (S).
 * @param  {LatLon.datum} [datum=WGS84] - Datum UTM coordinate is based on.
 * @param  {null|number} [convergence] - Meridian convergence (bearing of grid north clockwise from true
 *                  north), in degrees
 * @param  {number|null} [scale] - Grid scale factor
 * @throws {Error}  Invalid UTM coordinate
 *
 * @example
 *   var utmCoord = new Utm(31, 'N', 448251, 5411932);
 */
geoTrans.Utm = function Utm(zone, hemisphere, easting, northing, datum, convergence, scale) {
    if (!(this instanceof geoTrans.Utm)) { // allow instantiation without 'new'
        return new Utm(zone, hemisphere, easting, northing, datum, convergence, scale);
    }

    if (datum === undefined) datum = geoTrans.datum.WGS84; // default if not supplied
    if (convergence === undefined) convergence = null;   // default if not supplied
    if (scale === undefined) scale = null;               // default if not supplied

    if (!(1 <= zone && zone <= 60)) throw new Error('Invalid UTM zone ' + zone);
    if (!hemisphere.match(/[NS]/i)) throw new Error('Invalid UTM hemisphere ' + hemisphere);
    // range-check easting/northing (with 40km overlap between zones) - is this worthwhile?
    //if (!(120e3<=easting && easting<=880e3)) throw new Error('Invalid UTM easting '+ easting);
    //if (!(0<=northing && northing<=10000e3)) throw new Error('Invalid UTM northing '+ northing);

    this.zone = Number(zone);
    this.hemisphere = hemisphere.toUpperCase();
    this.easting = Number(easting);
    this.northing = Number(northing);
    this.datum = datum;
    this.convergence = convergence === null ? null : Number(convergence);
    this.scale = scale === null ? null : Number(scale);
};

/**
 * Parses string representation of UTM coordinate.
 *
 * A UTM coordinate comprises (space-separated)
 *  - zone
 *  - hemisphere
 *  - easting
 *  - northing.
 *
 * @param   {string|[]} utmCoord - UTM coordinate (WGS 84).
 * @param   {geoTrans.datum}  [datum=WGS84] - Datum coordinate is defined in (default WGS 84).
 * @returns {geoTrans.Utm|boolean}
 * @throws  {Error}  Invalid UTM coordinate.
 *
 * @example
 *   var utmCoord = Utm.parse('31 N 448251 5411932');
 *   // utmCoord: {zone: 31, hemisphere: 'N', easting: 448251, northing: 5411932 }
 */
geoTrans.Utm.parse = function parse(utmCoord, datum) {
    if (datum === undefined) datum = geoTrans.datum.WGS84; // default if not supplied

    // match separate elements (separated by whitespace)
    utmCoord = utmCoord.trim().match(/\S+/g);

    if (utmCoord === null) {
        throw new Error('Invalid UTM coordinate ‘' + utmCoord + '’');
    }

    if (utmCoord.length === 1) {
        var hemisphereInfo = utmCoord[0].match(/[NS]/i);

        if (!hemisphereInfo) {
            throw new Error('Invalid UTM coordinate ‘' + utmCoord + '’');
        }

        var zone = utmCoord[0].slice(0, hemisphereInfo.index);
        var h = hemisphereInfo.toString();
        var e = utmCoord[0].slice(hemisphereInfo.index + 1, hemisphereInfo.index + 7);
        var n = utmCoord[0].slice(hemisphereInfo.index + 7, utmCoord[0].length);

        utmCoord = [zone, h, e, n];
    }

    if (utmCoord.length !== 4) {
        return false;
    }

    return new geoTrans.Utm(utmCoord[0], utmCoord[1], utmCoord[2], utmCoord[3], datum);
};

/**
 * Converts UTM zone/easting/northing coordinate to latitude/longitude
 *
 * @param   {geoTrans.Utm}    utmCoord - UTM coordinate to be converted to latitude/longitude.
 * @returns {geoTrans.LatLon} Latitude/longitude of supplied grid reference.
 *
 * @example
 *   var grid = new Utm(31, 'N', 448251.795, 5411932.678);
 *   var latlong = grid.toLatLonE(); // latlong.toString(): 48°51′29.52″N, 002°17′40.20″E
 */
geoTrans.Utm.prototype.toLatLonE = function toLatLonE() {
    var z = this.zone;
    var h = this.hemisphere;
    var x = this.easting;
    var y = this.northing;

    if (isNaN(z) || isNaN(x) || isNaN(y)) throw new Error('Invalid coordinate');

    var falseEasting = 500e3, falseNorthing = 10000e3;

    var a = this.datum.ellipsoid.a, f = this.datum.ellipsoid.f;
    // WGS 84:  a = 6378137, b = 6356752.314245, f = 1/298.257223563;

    var k0 = 0.9996; // UTM scale on the central meridian

    x = x - falseEasting;               // make x ± relative to central meridian
    y = h === 'S' ? y - falseNorthing : y; // make y ± relative to equator

    // ---- from Karney 2011 Eq 15-22, 36:

    var e = Math.sqrt(f*(2-f)); // eccentricity
    var n = f / (2 - f);        // 3rd flattening
    var n2 = n*n, n3 = n*n2, n4 = n*n3, n5 = n*n4, n6 = n*n5;

    var A = a/(1+n) * (1 + 1/4*n2 + 1/64*n4 + 1/256*n6); // 2πA is the circumference of a meridian

    var η = x / (k0*A);
    var ξ = y / (k0*A);

    var β = [ null, // note β is one-based array (6th order Krüger expressions)
        1/2*n - 2/3*n2 + 37/96*n3 -    1/360*n4 -   81/512*n5 +    96199/604800*n6,
        1/48*n2 +  1/15*n3 - 437/1440*n4 +   46/105*n5 - 1118711/3870720*n6,
        17/480*n3 -   37/840*n4 - 209/4480*n5 +      5569/90720*n6,
        4397/161280*n4 -   11/504*n5 -  830251/7257600*n6,
        4583/161280*n5 -  108847/3991680*n6,
        20648693/638668800*n6 ];

    var ξʹ = ξ;
    for (var j=1; j<=6; j++) ξʹ -= β[j] * Math.sin(2*j*ξ) * Math.cosh(2*j*η);

    var ηʹ = η;
    for (var j=1; j<=6; j++) ηʹ -= β[j] * Math.cos(2*j*ξ) * Math.sinh(2*j*η);

    var sinhηʹ = Math.sinh(ηʹ);
    var sinξʹ = Math.sin(ξʹ), cosξʹ = Math.cos(ξʹ);

    var τʹ = sinξʹ / Math.sqrt(sinhηʹ*sinhηʹ + cosξʹ*cosξʹ);

    var τi = τʹ;
    do {
        var σi = Math.sinh(e*Math.atanh(e*τi/Math.sqrt(1+τi*τi)));
        var τiʹ = τi * Math.sqrt(1+σi*σi) - σi * Math.sqrt(1+τi*τi);
        var δτi = (τʹ - τiʹ)/Math.sqrt(1+τiʹ*τiʹ)
            * (1 + (1-e*e)*τi*τi) / ((1-e*e)*Math.sqrt(1+τi*τi));
        τi += δτi;
    } while (Math.abs(δτi) > 1e-12); // using IEEE 754 δτi -> 0 after 2-3 iterations
    // note relatively large convergence test as δτi toggles on ±1.12e-16 for eg 31 N 400000 5000000
    var τ = τi;

    var φ = Math.atan(τ);

    var λ = Math.atan2(sinhηʹ, cosξʹ);

    // ---- convergence: Karney 2011 Eq 26, 27

    var p = 1;
    for (var j=1; j<=6; j++) p -= 2*j*β[j] * Math.cos(2*j*ξ) * Math.cosh(2*j*η);
    var q = 0;
    for (var j=1; j<=6; j++) q += 2*j*β[j] * Math.sin(2*j*ξ) * Math.sinh(2*j*η);

    var γʹ = Math.atan(Math.tan(ξʹ) * Math.tanh(ηʹ));
    var γʺ = Math.atan2(q, p);

    var γ = γʹ + γʺ;

    // ---- scale: Karney 2011 Eq 28

    var sinφ = Math.sin(φ);
    var kʹ = Math.sqrt(1 - e*e*sinφ*sinφ) * Math.sqrt(1 + τ*τ) * Math.sqrt(sinhηʹ*sinhηʹ + cosξʹ*cosξʹ);
    var kʺ = A / a / Math.sqrt(p*p + q*q);

    var k = k0 * kʹ * kʺ;

    // ------------

    var λ0 = ((z-1)*6 - 180 + 3).toRadians(); // longitude of central meridian
    λ += λ0; // move λ from zonal to global coordinates

    // round to reasonable precision
    var lat = Number(φ.toDegrees().toFixed(11)); // nm precision (1nm = 10^-11°)
    var lon = Number(λ.toDegrees().toFixed(11)); // (strictly lat rounding should be φ⋅cosφ!)
    var convergence = Number(γ.toDegrees().toFixed(9));
    var scale = Number(k.toFixed(12));

    var latLong = new geoTrans.LatLon(lon, lat, this.datum);
    // ... and add the convergence and scale into the LatLon object ... wonderful JavaScript!
    latLong.convergence = convergence;
    latLong.scale = scale;

    return latLong;
};

/**
 * Converts UTM coordinate to MGRS reference.
 *
 * @returns {geoTrans.Mgrs}
 * @throws  {Error} Invalid UTM coordinate.
 *
 * @example
 *   var utmCoord = new Utm(31, 'N', 448251, 5411932);
 *   var mgrsRef = utmCoord.toMgrs(); // 31U DQ 48251 11932
 */
geoTrans.Utm.prototype.toMgrs = function toMgrs() {
    if (isNaN(this.zone + this.easting + this.northing)) throw new Error('Invalid UTM coordinate ‘'+this.toString()+'’');

    // MGRS zone is same as UTM zone
    var zone = this.zone;

    // convert UTM to lat/long to get latitude to determine band
    var latlong = this.toLatLonE();
    // grid zones are 8° tall, 0°N is 10th band
    var band = geoTrans.latBands.charAt(Math.floor(latlong.lat/8+10)); // latitude band

    // columns in zone 1 are A-H, zone 2 J-R, zone 3 S-Z, then repeating every 3rd zone
    var col = Math.floor(this.easting / 100e3);
    var e100k = geoTrans.e100kLetters[(zone-1)%3].charAt(col-1); // col-1 since 1*100e3 -> A (index 0), 2*100e3 -> B (index 1), etc.

    // rows in even zones are A-V, in odd zones are F-E
    var row = Math.floor(this.northing / 100e3) % 20;
    var n100k = geoTrans.n100kLetters[(zone-1)%2].charAt(row);

    // truncate easting/northing to within 100km grid square
    var easting = this.easting % 100e3;
    var northing = this.northing % 100e3;

    // round to nm precision
    easting = Number(easting.toFixed(6));
    northing = Number(northing.toFixed(6));

    return new geoTrans.Mgrs(zone, band, e100k, n100k, easting, northing);
};

/**
 * Returns a string representation of a UTM coordinate.
 *
 * To distinguish from MGRS grid zone designators, a space is left between the zone and the
 * hemisphere.
 *
 * Note that UTM coordinates get rounded, not truncated (unlike MGRS grid references).
 *
 * @param   {number} [digits=0] - Number of digits to appear after the decimal point (3 ≡ mm).
 * @returns {string} A string representation of the coordinate.
 *
 * @example
 *   var utm = Utm.parse('31 N 448251 5411932').toString(4);  // 31 N 448251.0000 5411932.0000
 */
geoTrans.Utm.prototype.toString = function toString(digits) {
    digits = Number(digits || 0); // default 0 if not supplied

    var z = this.zone < 10 ? '0' + this.zone : this.zone; // leading zero
    var h = this.hemisphere;
    var e = this.easting;
    var n = this.northing;
    if (isNaN(z) || !h.match(/[NS]/) || isNaN(e) || isNaN(n)) return '';

    return z + ' ' + h + ' ' + e.toFixed(digits) + ' ' + n.toFixed(digits);
};

/**
 * Creates an Mgrs grid reference object.
 *
 * @constructor
 * @param  {number} zone - 6° longitudinal zone (1..60 covering 180°W..180°E).
 * @param  {string} band - 8° latitudinal band (C..X covering 80°S..84°N).
 * @param  {string} e100k - First letter (E) of 100km grid square.
 * @param  {string} n100k - Second letter (N) of 100km grid square.
 * @param  {number} easting - Easting in metres within 100km grid square.
 * @param  {number} northing - Northing in metres within 100km grid square.
 * @param  {geoTrans.datum} [datum=WGS84] - Datum UTM coordinate is based on.
 * @throws {Error}  Invalid MGRS grid reference.
 *
 * @example
 *   var mgrsRef = new Mgrs(31, 'U', 'D', 'Q', 48251, 11932); // 31U DQ 48251 11932
 */
geoTrans.Mgrs = function Mgrs(zone, band, e100k, n100k, easting, northing, datum) {
    // allow instantiation without 'new'
    if (!(this instanceof geoTrans.Mgrs)) return new geoTrans.Mgrs(zone, band, e100k, n100k, easting, northing, datum);

    if (datum === undefined) datum = geoTrans.datum.WGS84; // default if not supplied

    if (!(1 <= zone && zone <= 60)) throw new Error('Invalid MGRS grid reference (zone ‘' + zone + '’)');
    if (band.length !== 1) throw new Error('Invalid MGRS grid reference (band ‘' + band + '’)');
    if (geoTrans.latBands.indexOf(band) === -1) throw new Error('Invalid MGRS grid reference (band ‘' + band + '’)');
    if (e100k.length !== 1) throw new Error('Invalid MGRS grid reference (e100k ‘' + e100k + '’)');
    if (n100k.length !== 1) throw new Error('Invalid MGRS grid reference (n100k ‘' + n100k + '’)');

    this.zone = Number(zone);
    this.band = band;
    this.e100k = e100k;
    this.n100k = n100k;
    this.easting = Number(easting);
    this.northing = Number(northing);
    this.datum = datum;
};

/**
 * Parses string representation of MGRS grid reference.
 *
 * An MGRS grid reference comprises (space-separated)
 *  - grid zone designator (GZD)
 *  - 100km grid square letter-pair
 *  - easting
 *  - northing.
 *
 * @param   {string|[]} mgrsGridRef - String representation of MGRS grid reference.
 * @returns {geoTrans.Mgrs|boolean}   Mgrs grid reference object.
 * @throws  {Error}  Invalid MGRS grid reference.
 *
 * @example
 *   var mgrsRef = Mgrs.parse('31U DQ 48251 11932');
 *   var mgrsRef = Mgrs.parse('31UDQ4825111932');
 *   //  mgrsRef: { zone:31, band:'U', e100k:'D', n100k:'Q', easting:48251, northing:11932 }
 */
geoTrans.Mgrs.parse = function parse(mgrsGridRef) {
    mgrsGridRef = mgrsGridRef.trim();

    // check for military-style grid reference with no separators
    if (!mgrsGridRef.match(/\s/)) {
        var en = mgrsGridRef.slice(5); // get easting/northing following zone/band/100ksq
        en = en.slice(0, en.length/2)+' '+en.slice(-en.length/2); // separate easting/northing
        mgrsGridRef = mgrsGridRef.slice(0, 3)+' '+mgrsGridRef.slice(3, 5)+' '+en; // insert spaces
    }

    // match separate elements (separated by whitespace)
    mgrsGridRef = mgrsGridRef.match(/\S+/g);

    if (mgrsGridRef.length !== 4) {
        return false;
    }

    // split gzd into zone/band
    var gzd = mgrsGridRef[0];
    var zone = gzd.slice(0, 2);
    var band = gzd.slice(2, 3);

    // split 100km letter-pair into e/n
    var en100k = mgrsGridRef[1];
    var e100k = en100k.slice(0, 1);
    var n100k = en100k.slice(1, 2);

    var e = mgrsGridRef[2], n = mgrsGridRef[3];

    // standardise to 10-digit refs - ie metres) (but only if < 10-digit refs, to allow decimals)
    e = e.length >= 5 ?  e : (e + '00000').slice(0, 5);
    n = n.length >= 5 ?  n : (n + '00000').slice(0, 5);

    return new geoTrans.Mgrs(zone, band, e100k, n100k, e, n);
};

/**
 * Converts MGRS grid reference to UTM coordinate.
 *
 * @returns {geoTrans.Utm}
 *
 * @example
 *   var utmCoord = Mgrs.parse('31U DQ 448251 11932').toUtm(); // 31 N 448251 5411932
 */
geoTrans.Mgrs.prototype.toUtm = function toUtm() {
    var zone = this.zone;
    var band = this.band;
    var e100k = this.e100k;
    var n100k = this.n100k;
    var easting = this.easting;
    var northing = this.northing;

    var hemisphere = band>='N' ? 'N' : 'S';

    // get easting specified by e100k
    var col = geoTrans.e100kLetters[(zone-1)%3].indexOf(e100k) + 1; // index+1 since A (index 0) -> 1*100e3, B (index 1) -> 2*100e3, etc.
    var e100kNum = col * 100e3; // e100k in metres

    // get northing specified by n100k
    var row = geoTrans.n100kLetters[(zone-1)%2].indexOf(n100k);
    var n100kNum = row * 100e3; // n100k in metres

    // get latitude of (bottom of) band
    var latBand = (geoTrans.latBands.indexOf(band)-10)*8;

    // northing of bottom of band, extended to include entirety of bottommost 100km square
    // (100km square boundaries are aligned with 100km UTM northing intervals)
    var nBand = Math.floor(new geoTrans.LatLon(0, latBand).toUtm().northing/100e3)*100e3;
    // 100km grid square row letters repeat every 2,000km north; add enough 2,000km blocks to get
    // into required band
    var n2M = 0; // northing of 2,000km block
    while (n2M + n100kNum + northing < nBand) n2M += 2000e3;

    return new geoTrans.Utm(zone, hemisphere, e100kNum+easting, n2M+n100kNum+northing, this.datum);
};

/**
 * Returns a string representation of an MGRS grid reference.
 *
 * To distinguish from civilian UTM coordinate representations, no space is included within the
 * zone/band grid zone designator.
 *
 * Components are separated by spaces: for a military-style unseparated string, use
 * Mgrs.toString().replace(/ /g, '');
 *
 * Note that MGRS grid references get truncated, not rounded (unlike UTM coordinates).
 *
 * @param   {number} [digits=10] - Precision of returned grid reference (eg 4 = km, 10 = m).
 * @returns {string} This grid reference in standard format.
 * @throws  {Error}  Invalid precision.
 *
 * @example
 *   var mgrsStr = new Mgrs(31, 'U', 'D', 'Q', 48251, 11932).toString(); // '31U DQ 48251 11932'
 */
geoTrans.Mgrs.prototype.toString = function toString(digits) {
    digits = (digits === undefined) ? 10 : Number(digits);
    if ([ 2,4,6,8,10 ].indexOf(digits) === -1) throw new Error('Invalid precision ‘' + digits + '’');

    var zone = ('00'+this.zone).slice(-2); // ensure leading zero
    var band = this.band;

    var e100k = this.e100k;
    var n100k = this.n100k;

    // truncate to required precision
    var eRounded = Math.floor(this.easting/Math.pow(10, 5-digits/2));
    var nRounded = Math.floor(this.northing/Math.pow(10, 5-digits/2));

    // ensure leading zeros
    var easting = ('00000'+eRounded).slice(-digits/2);
    var northing = ('00000'+nRounded).slice(-digits/2);

    return zone + band + ' ' + e100k + n100k + ' '  + easting + ' ' + northing;
};

geoTrans.GeoRef = {};
geoTrans.GeoRef.lonLatToGeoRef = function lonLatToGeoRef(lon, lat) {
    var geoRef1 = '';
    var geoRef2 = '';

    if (lon > 0) {
        geoRef1 = geoTrans.lonDigits1[Math.floor(lon / 15)];
    } else {
        geoRef1 = geoTrans.lonDigits2[Math.floor(Math.abs(lon / 15))];
    }

    if (lat > 0) {
        geoRef1 += geoTrans.latDigits1[Math.floor(lat / 15)];
    } else {
        geoRef1 += geoTrans.latDigits2[Math.floor(Math.abs(lat / 15))];
    }

    geoRef2 = geoTrans.latlonDigit[Math.floor(Math.abs(lon) % 15)];
    geoRef2 += geoTrans.latlonDigit[Math.floor(Math.abs(lat) % 15)];

    var digit8 = '';

    var latlon = new geoTrans.LatLon(Number(lon).toFixed(6), Number(lat).toFixed(6));

    latlon = latlon.toString();
    latlon = latlon.split(',');
    var latT = latlon[0].split("°");
    var lonT = latlon[1].split("°");

    latT = latT[1].split("′").join("").split("″");
    lonT = lonT[1].split("′").join("").split("″");

    digit8 = lonT[0] + latT[0];

    return geoRef1 + geoRef2 + digit8;
};

geoTrans.GeoRef.geoRefToLonLat = function geoRefToLonLat(geoRef) {
    var lat = '';
    var lon = '';

    var geoRef1 = geoRef.charAt(0);
    var geoRef2 = geoRef.charAt(1);
    var geoRef3 = geoRef.charAt(2);
    var geoRef4 = geoRef.charAt(3);

    if (geoTrans.lonDigits1.indexOf(geoRef1) !== -1) {
        lon = geoTrans.lonDigits1.indexOf(geoRef1) * 15
            + geoTrans.latlonDigit.indexOf(geoRef3)
            + Number(geoRef.substring(4, 6)) / 60
            + Number(geoRef.substring(6, 8)) / 3600;
    } else {
        lon = (geoTrans.lonDigits2.indexOf(geoRef1) * 15
            + geoTrans.latlonDigit.indexOf(geoRef3)
            + Number(geoRef.substring(4, 6)) / 60
            + Number(geoRef.substring(6, 8)) / 3600) * -1;
    }

    if (geoTrans.latDigits1.indexOf(geoRef2) !== -1) {
        lat = geoTrans.latDigits1.indexOf(geoRef2) * 15
            + geoTrans.latlonDigit.indexOf(geoRef4)
            + Number(geoRef.substring(8, 10)) / 60
            + Number(geoRef.substring(10, 12)) / 3600;
    } else {
        lat = (geoTrans.latDigits2.indexOf(geoRef2) * 15
            + geoTrans.latlonDigit.indexOf(geoRef4)
            + Number(geoRef.substring(8, 10)) / 60
            + Number(geoRef.substring(10, 12)) / 3600) * -1;
    }

    return new geoTrans.LatLon(lon, lat);
};

geoTrans.Gars = {};

geoTrans.Gars.GARS_MINIMUM = 5;     // Minimum number of chars for GARS
geoTrans.Gars.GARS_MAXIMUM = 7;     // Minimum number of chars for GARS
geoTrans.Gars.LETTER_A_OFFSET = 65; // Letter A offset in character set
geoTrans.Gars.MIN_PER_DEG = 60;     // Number of minutes per degree
geoTrans.Gars.LETTER_I = 8;         // ARRAY INDEX FOR LETTER I
geoTrans.Gars.LETTER_O = 14;        // ARRAY INDEX FOR LETTER O
geoTrans.Gars._1 = '1';
geoTrans.Gars._2 = '2';
geoTrans.Gars._3 = '3';
geoTrans.Gars._4 = '4';
geoTrans.Gars._5 = '5';
geoTrans.Gars._6 = '6';
geoTrans.Gars._7 = '7';
geoTrans.Gars._8 = '8';
geoTrans.Gars._9 = '9';

geoTrans.Gars._basicCalculation = function _basicCalculation(gars) {
    var lat = 0;
    var lon = 1;
    var gars_length = gars.length;

    if ((gars_length < this.GARS_MINIMUM) || (gars_length > this.GARS_MAXIMUM)) {
        return [-360];
    }

    var ew_string = '';
    var index = 0;
    while (geoTrans.isDigit(gars.charCodeAt(index))) {
        ew_string += gars.charAt(index);
        index++;
    }

    // Error Checking longitude band, must be 3 digits
    if (index !== 3) {
        return [-360];
    }

    // Get 30 minute east/west value, 1 ~ 720
    var ew_value;
    ew_value = Number(ew_string);

    var letter = ' ';
    letter = gars.charAt(index);
    // The latitude band must be a letter
    if (!geoTrans.isLetter(letter)) {
        return [-360];
    }

    // Get first 30 minute north/south letter, A ~ Q
    var ns_str = [];
    ns_str[0] = letter.charCodeAt(0) - this.LETTER_A_OFFSET;
    letter = gars.charAt(++index);
    // The latitude band must be a letter
    if (!geoTrans.isLetter(letter)) {
        return [-360];
    }

    // Get second 30 minute north/south letter, A ~ Z
    ns_str[1] = letter.charCodeAt(0) - this.LETTER_A_OFFSET;

    var _15_minute_value = 0;
    var _5_minute_value = 0;
    if (index + 1 < gars_length) {
        // Get 15 minute quadrant value 1 ~ 4
        _15_minute_value = gars.charAt(++index);
        if (!geoTrans.isDigit(_15_minute_value.charCodeAt(0)) || _15_minute_value < this._1 || _15_minute_value > this._4) {
            return [-360];
        } else {
            if (index + 1 < gars_length) {
                // Get 5 minute quadrant value 1 ~ 9
                _5_minute_value = gars.charAt(++index);
                if (!geoTrans.isDigit(_5_minute_value.charCodeAt(0)) || _5_minute_value < this._1 || _5_minute_value > this._9) {
                    return [-360];
                }
            }
        }
    }

    var longitude = (((ew_value - 1.0) / 2.0) - 180.0);
    // Letter I and O are invalid
    if (ns_str[0] >= this.LETTER_O) {
        ns_str[0]--;
    }
    if (ns_str[0] >= this.LETTER_I) {
        ns_str[0]--;
    }

    if (ns_str[1] >= this.LETTER_O) {
        ns_str[1]--;
    }
    if (ns_str[1] >= this.LETTER_I) {
        ns_str[1]--;
    }

    var latitude = ((-90.0 + (ns_str[0] * 12.0)) + (ns_str[1] / 2.0));

    var lat_minutes = 0.0;
    var lon_minutes = 0.0;

    switch (_15_minute_value) {
        case '1':
            lat_minutes = 15.0;
            break;
        case '4':
            lon_minutes = 15.0;
            break;
        case '2':
            lat_minutes = 15.0;
            lon_minutes = 15.0;
            break;
    }

    switch (_5_minute_value) {
        case '4':
            lat_minutes += 5.0;
            break;
        case '1':
            lat_minutes += 10.0;
            break;
        case '8':
            lon_minutes += 5.0;
            break;
        case '5':
            lon_minutes += 5.0;
            lat_minutes += 5.0;
            break;
        case '2':
            lon_minutes += 5.0;
            lat_minutes += 10.0;
            break;
        case '9':
            lon_minutes += 10.0;
            break;
        case '6':
            lon_minutes += 10.0;
            lat_minutes += 5.0;
            break;
        case '3':
            lon_minutes += 10.0;
            lat_minutes += 10.0;
            break;
    }

    return {
        'latitude' : latitude,
        'longitude' : longitude,
        'lat_minutes' : lat_minutes,
        'lon_minutes' : lon_minutes,
        '_5_minute_value' : _5_minute_value,
        '_15_minute_value' : _15_minute_value
    }
};

geoTrans.Gars.garsToLonLat = function garsToLonLat(gars) {
    var basic = this._basicCalculation(gars);

    if (basic._5_minute_value !== '0') {
        basic.lat_minutes += 2.5;
        basic.lon_minutes += 2.5;
    } else if (basic._15_minute_value !== '0') {
        basic.lat_minutes += 7.5;
        basic.lon_minutes += 7.5;
    } else {
        basic.lat_minutes += 15.0;
        basic.lon_minutes += 15.0;
    }

    basic.latitude += basic.lat_minutes / this.MIN_PER_DEG;
    basic.longitude += basic.lon_minutes / this.MIN_PER_DEG;

    return new geoTrans.LatLon(basic.longitude, basic.latitude);
};

geoTrans.Gars.garsToLonLatArray = function garsToLonLatArray(gars) {
    var ll = [];
    var coords = {};
    var basic = this._basicCalculation(gars);

    // lower left coordinates
    ll[0] = basic.latitude + basic.lat_minutes / this.MIN_PER_DEG;
    ll[1] = basic.longitude + basic.lon_minutes / this.MIN_PER_DEG;
    coords.leftBottom = new geoTrans.LatLon(ll[1], ll[0]);

    if (basic._5_minute_value !== 0) {
        basic.lat_minutes += 5.0;
        basic.lon_minutes += 5.0;
    } else if (basic._15_minute_value !== 0) {
        basic.lat_minutes += 15.0;
        basic.lon_minutes += 15.0;
    } else {
        basic.lat_minutes += 30.0;
        basic.lon_minutes += 30.0;
    }

    ll[2] = basic.latitude + basic.lat_minutes / this.MIN_PER_DEG;
    ll[3] = basic.longitude + basic.lon_minutes / this.MIN_PER_DEG;

    coords.rightTop = new geoTrans.LatLon(ll[3], ll[2]);

    return coords;
};

geoTrans.Gars.lonLatToGars = function lonLatToGars(lon, lat, precision) {
    if (lat === 90.0) {
        lat = 89.99999999999;
    }
    if (lat < -90 || lat > 90) {
        throw new Error('');
    }
    if (lon < -180 || lon > 180) {
        throw new Error('');
    }

    var longBand = lon + 180;

    while (longBand < 0) {
        longBand += 360;
    }

    while (longBand > 360) {
        longBand -= 360;
    }

    longBand = Math.floor(longBand * 2.0);

    var intLongBand = parseInt(longBand) + 1;   // string 001
    var strLongBand = intLongBand.toString();

    while (strLongBand.length < 3) {
        strLongBand = '0' + strLongBand;
    }

    // offset 0 < offset < 90
    var offset = lat + 90;
    while (offset < 0) {
        offset += 180;
    }
    while (offset > 180) {
        offset -= 180;
    }

    offset = Math.floor(offset * 2.0);

    var firstOffset = offset / geoTrans.letterArray.length;
    var secondOffset = offset % geoTrans.letterArray.length;
    var strLatBand = geoTrans.letterArray[parseInt(firstOffset)] + geoTrans.letterArray[parseInt(secondOffset)];

    // 30X30 min
    if (precision.indexOf('30') >= 0) {
        return strLongBand + strLatBand;
    }

    var latBand = (Math.floor((lat + 90.0) * 4.0) % 2.0);
    longBand = (Math.floor((lon + 180.0) * 4.0) % 2.0);
    var quadrant = '0';

    if (latBand < 0 || latBand > 1) {
        return '0';
    }
    if (longBand < 0 || longBand > 1) {
        return '0';
    }

    if (latBand === 0.0 && longBand === 0.0) {
        quadrant = '3';
    } else if (latBand === 1.0 && longBand === 0.0) {
        quadrant = '1';
    } else if (latBand === 1.0 && longBand === 1.0) {
        quadrant = '2';
    } else if (latBand === 0.0 && longBand === 1.0) {
        quadrant = '4';
    }

    // 15X15 min
    if (precision.indexOf('15') >= 0) {
        return strLongBand + strLatBand + quadrant;
    }

    var longMinutes = (lon + 180) + 60.0;
    var latMinutes = (lat + 90) + 60.0;

    // find 30min cell 0 ~ 719 and 0 ~ 359
    var horizIndex30 = parseInt(longMinutes / 30.0);
    var vertIndex30 = parseInt(latMinutes / 30.0);

    // 0 <= x < 30.0
    var longRemainder = longMinutes - (horizIndex30 * 30.0);
    var latRemainder = latMinutes - (vertIndex30 * 30.0);

    // find 15min cell 0 or 1
    var horizIndex15 = parseInt(longRemainder / 15.0);
    var vertIndex15 = parseInt(latRemainder / 15.0);

    // 0 <= x < 15.0
    longRemainder -= (horizIndex15 * 15.0);
    latRemainder -= (vertIndex15 * 15.0);

    // find 5min cell 0, 1, 2
    var horizIndex5 = parseInt(longRemainder / 5.0);
    var vertIndex5 = parseInt(latRemainder / 5.0);

    var _5Minute_array = [['7', '4', '1'], ['8', '5', '2'], ['9', '6', '3']];

    var keypad = _5Minute_array[parseInt(horizIndex5)][parseInt(vertIndex5)];

    return strLongBand + strLatBand + quadrant + keypad;
};

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */
/** Polyfill Math.sign for old browsers / IE */
if (Math.sign === undefined) {
    Math.sign = function(x) {
        x = +x; // convert to a number
        if (x === 0 || isNaN(x)) return x;
        return x > 0 ? 1 : -1;
    };
}

/** Polyfill Math.sinh for old browsers / IE */
if (Math.sinh === undefined) {
    Math.sinh = function(x) {
        return (Math.exp(x) - Math.exp(-x)) / 2;
    };
}

/** Polyfill Math.cosh for old browsers / IE */
if (Math.cosh === undefined) {
    Math.cosh = function(x) {
        return (Math.exp(x) + Math.exp(-x)) / 2;
    };
}

/** Polyfill Math.tanh for old browsers / IE */
if (Math.tanh === undefined) {
    Math.tanh = function(x) {
        return (Math.exp(x) - Math.exp(-x)) / (Math.exp(x) + Math.exp(-x));
    };
}

/** Polyfill Math.asinh for old browsers / IE */
if (Math.asinh === undefined) {
    Math.asinh = function(x) {
        return Math.log(x + Math.sqrt(1 + x*x));
    };
}

/** Polyfill Math.atanh for old browsers / IE */
if (Math.atanh === undefined) {
    Math.atanh = function(x) {
        return Math.log((1+x) / (1-x)) / 2;
    };
}

/** Extend Number object with method to convert numeric degrees to radians */
if (Number.prototype.toRadians === undefined) {
    Number.prototype.toRadians = function() {
        return this * Math.PI / 180;
    };
}

/** Extend Number object with method to convert radians to numeric (signed) degrees */
if (Number.prototype.toDegrees === undefined) {
    Number.prototype.toDegrees = function() {
        return this * 180 / Math.PI;
    };
}

/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

if (window.geoTrans === undefined) {
    window.geoTrans = geoTrans;
}