//
//  GMSDatasetFeature.h
//  Google Maps SDK for iOS
//
//  Copyright 2024 Google LLC
//
//  Usage of this SDK is subject to the Google Maps/Google Earth APIs Terms of
//  Service: https://cloud.google.com/maps-platform/terms
//

#import <Foundation/Foundation.h>

#import "GMSFeature.h"

NS_ASSUME_NONNULL_BEGIN
NS_SWIFT_NAME(DatasetFeature)
/**
 * An interface representing a feature from a dataset.
 *
 * The featureType of a DatasetFeature will always be `GMSFeatureTypeDataset`.
 */
@interface GMSDatasetFeature : NSObject <GMSFeature>

/** Dataset id of the dataset that this feature belongs to. */
@property(nonatomic, readonly) NSString *datasetID;

/** Key-value mapping of the attributes associated with the feature in the dataset. */
@property(nonatomic, readonly) NSDictionary<NSString *, NSString *> *datasetAttributes;

/**
 * Create a dataset feature instance for testing uses.
 *
 * This method should be used for testing purposes only; `GMSDatasetFeature` instances should only
 * be created by the SDK in production code.
 */
- (instancetype)initWithDatasetID:(NSString *)datasetID
                datasetAttributes:(NSDictionary<NSString *, NSString *> *)datasetAttributes
    NS_DESIGNATED_INITIALIZER;

- (instancetype)init NS_DESIGNATED_INITIALIZER NS_UNAVAILABLE;

@end
NS_ASSUME_NONNULL_END
