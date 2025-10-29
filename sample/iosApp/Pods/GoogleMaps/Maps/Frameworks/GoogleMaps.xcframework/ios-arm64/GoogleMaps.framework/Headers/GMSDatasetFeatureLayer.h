//
//  GMSDatasetFeatureLayer.h
//  Google Maps SDK for iOS
//
//  Copyright 2024 Google LLC
//
//  Usage of this SDK is subject to the Google Maps/Google Earth APIs Terms of
//  Service: https://cloud.google.com/maps-platform/terms
//

#import <Foundation/Foundation.h>

#import "GMSDatasetFeature.h"
#import "GMSFeatureLayer.h"

/**
 * An interface representing a feature layer for a dataset.
 *
 * The featureType of a `GMSDatasetFeatureLayer` will always be `GMSFeatureTypeDataset`.
 */
NS_SWIFT_NAME(DatasetFeatureLayer)
@interface GMSDatasetFeatureLayer : GMSFeatureLayer <GMSDatasetFeature *>

/** ID of the dataset. */
@property(nonatomic, readonly) NSString *datasetID;

@end
