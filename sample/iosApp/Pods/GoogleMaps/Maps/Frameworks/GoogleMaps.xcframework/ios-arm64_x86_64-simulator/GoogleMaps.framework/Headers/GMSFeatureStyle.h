//
//  GMSFeatureStyle.h
//  Google Maps SDK for iOS
//
//  Copyright 2022 Google LLC
//
//  Usage of this SDK is subject to the Google Maps/Google Earth APIs Terms of
//  Service: https://cloud.google.com/maps-platform/terms
//


#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@class GMSMutableFeatureStyle;

/** Specifies how a map feature should appear when displayed on a map. */
NS_SWIFT_NAME(FeatureStyle)
@interface GMSFeatureStyle : NSObject <NSCopying, NSMutableCopying>

/** Creates a new style. */
+ (instancetype)styleWithFillColor:(nullable UIColor *)fillColor
                       strokeColor:(nullable UIColor *)strokeColor
                       strokeWidth:(CGFloat)strokeWidth
    NS_SWIFT_UNAVAILABLE("Use initializer instead");

/** Initializes a new style. */
- (instancetype)initWithFillColor:(nullable UIColor *)fillColor
                      strokeColor:(nullable UIColor *)strokeColor
                      strokeWidth:(CGFloat)strokeWidth NS_DESIGNATED_INITIALIZER;

/** Initializes a default style. */
- (instancetype)init NS_DESIGNATED_INITIALIZER;

/** Specifies the fill color, including the alpha channel. */
@property(nonatomic, readonly, copy, nullable) UIColor *fillColor;

/** Specifies the border color, including the alpha channel. */
@property(nonatomic, readonly, copy, nullable) UIColor *strokeColor;

/** Specifies the border width, in screen points. */
@property(nonatomic, readonly) CGFloat strokeWidth;

/** Specifies the radius of a point. Only applies to point geometries. */
@property(nonatomic, readonly) CGFloat pointRadius;

/* Returns a copy of this style. */
- (GMSFeatureStyle *)copy;

/* Returns a mutable copy of this style. */
- (GMSMutableFeatureStyle *)mutableCopy;

@end

/** Mutable version of GMSFeatureStyle. */
NS_SWIFT_NAME(MutableFeatureStyle)
@interface GMSMutableFeatureStyle : GMSFeatureStyle

@property(nonatomic, readwrite, copy, nullable) UIColor *fillColor;
@property(nonatomic, readwrite, copy, nullable) UIColor *strokeColor;
@property(nonatomic, readwrite) CGFloat strokeWidth;
@property(nonatomic, readwrite) CGFloat pointRadius;

/** Creates a new mutable style. */
+ (instancetype)style NS_SWIFT_UNAVAILABLE("Use initializer instead");

@end

/** Value to use for `strokeWidth` parameter when the stroke width should be unchanged. */
FOUNDATION_EXTERN const CGFloat
    GMSFeatureStyleStrokeWidthUnspecified NS_SWIFT_NAME(FeatureStyle.strokeWidthUnspecified);

/** Value to use for `pointRadius` parameter when the point radius should be unchanged. */
FOUNDATION_EXTERN const CGFloat
    GMSFeatureStylePointRadiusUnspecified NS_SWIFT_NAME(FeatureStyle.pointRadiusUnspecified);

NS_ASSUME_NONNULL_END
